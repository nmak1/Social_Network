package ru.netology.social_network.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.social_network.api.EventApiService
import ru.netology.social_network.dao.EventDao
import ru.netology.social_network.dao.EventRemoteKeyDao
import ru.netology.social_network.db.AppDb
import ru.netology.social_network.dto.*
import ru.netology.social_network.entity.EventEntity
import ru.netology.social_network.enumeration.AttachmentType
import ru.netology.social_network.errors.ApiError
import ru.netology.social_network.errors.AppError
import ru.netology.social_network.errors.NetworkError
import java.io.IOException
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(

    private val eventDao: EventDao,
    private val eventApiService: EventApiService,
    eventRemoteKeyDao: EventRemoteKeyDao,
    appDb: AppDb,
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { eventDao.getPagingSource() },
        remoteMediator = EventRemoteMediator(
            eventDao,
            eventApiService,
            eventRemoteKeyDao,
            appDb,
        )
    ).flow
        .map {
            it.map(EventEntity::toDto)
        }

    override suspend fun saveEvent(event: Event) {
        try {
//            eventDao.saveEvent(EventEntity.fromDto(event))
            val response = eventApiService.saveEvent(event)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())
            eventDao.insertEvent(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun saveWithAttachment(event: Event, upload: MediaUpload) {
        try {
            val media = uploadWithContent(upload)
            val eventWithAttachment =
                event.copy(attachment = Attachment(media.url, AttachmentType.IMAGE))
            saveEvent(eventWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun uploadWithContent(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file",
                "name",
                upload.inputStream.readBytes()
                    .toRequestBody("*/*".toMediaTypeOrNull())
            )

            val response = eventApiService.uploadMedia(media)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            return response.body() ?: throw ApiError(response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            eventDao.removeById(id)
            val response = eventApiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            eventDao.likeById(id)
            val response = eventApiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())

            eventDao.insertEvent(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun unlikeById(id: Long) {
        try {
            eventDao.unlikeById(id)
            val response = eventApiService.unlikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())

            eventDao.insertEvent(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun participate(id: Long) {
        try {
            eventDao.participate(id)
            val response = eventApiService.participate(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())

            eventDao.insertEvent(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun doNotParticipate(id: Long) {
        try {
            eventDao.doNotParticipate(id)
            val response = eventApiService.doNotParticipate(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())

            eventDao.insertEvent(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}