package ru.netology.social_network.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.social_network.api.WallApiService
import ru.netology.social_network.dao.*
import ru.netology.social_network.db.AppDb
import ru.netology.social_network.dto.Post
import ru.netology.social_network.entity.PostEntity
import javax.inject.Inject

class WallRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val wallApiService: WallApiService,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : WallRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun loadUserWall(userId: Long): Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(
            wallApiService = wallApiService,
            postDao = postDao,
            postRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb,
            authorId = userId,
        ),
        pagingSourceFactory = { postDao.getPagingSource(userId) }
    ).flow
        .map {
            it.map(PostEntity::toDto)
        }
}