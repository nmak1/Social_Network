package ru.netology.social_network.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.social_network.api.EventApiService
import ru.netology.social_network.dao.EventDao
import ru.netology.social_network.dao.EventRemoteKeyDao
import ru.netology.social_network.db.AppDb
import ru.netology.social_network.entity.EventEntity
import ru.netology.social_network.entity.EventRemoteKeyEntity
import ru.netology.social_network.errors.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val eventDao: EventDao,
    private val eventApiService: EventApiService,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>,
    ): MediatorResult {
        try {
            val result = when (loadType) {
                LoadType.REFRESH -> {
                    eventApiService.getEventLatest(state.config.pageSize)
                }
                LoadType.APPEND -> {
                    val id =
                        eventRemoteKeyDao.min() ?: return MediatorResult.Success(
                            false
                        )
                    eventApiService.getEventBefore(id, state.config.pageSize)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)
                }
            }

            if (!result.isSuccessful) {
                throw ApiError(result.message())
            }

            if (result.body().isNullOrEmpty())
                return MediatorResult.Success(true)

            val body = result.body() ?: throw ApiError(result.message())

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.KeyType.AFTER,
                                body.first().id
                            )
                        )

                        if (eventRemoteKeyDao.isEmpty()) {
                            eventRemoteKeyDao.insert(
                                EventRemoteKeyEntity(
                                    EventRemoteKeyEntity.KeyType.BEFORE,
                                    body.last().id
                                )
                            )
                        }
                        eventDao.removeAll()
                    }

                    LoadType.APPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.KeyType.BEFORE,
                                body.last().id,
                            ),
                        )
                    }

                    LoadType.PREPEND -> {
                        eventRemoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.KeyType.AFTER,
                                body.first().id,
                            ),
                        )
                    }
                }
                eventDao.insertEvents(body.map(EventEntity::fromDto))
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}