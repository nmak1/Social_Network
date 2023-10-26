package ru.netology.social_network.repository

import androidx.paging.*
import kotlinx.coroutines.flow.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.social_network.api.PostApiService
import ru.netology.social_network.dao.PostDao
import ru.netology.social_network.dao.PostRemoteKeyDao
import ru.netology.social_network.db.AppDb
import ru.netology.social_network.dto.*
import ru.netology.social_network.entity.PostEntity
import ru.netology.social_network.enumeration.AttachmentType
import ru.netology.social_network.errors.ApiError
import ru.netology.social_network.errors.AppError
import ru.netology.social_network.errors.NetworkError
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postApiService: PostApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { postDao.getPagingSource() },
        remoteMediator = PostRemoteMediator(
            postDao,
            postApiService,
            postRemoteKeyDao,
            appDb,
        )
    ).flow
        .map {
            it.map(PostEntity::toDto)
        }

    override suspend fun savePost(post: Post) {
        try {
            val response = postApiService.savePost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())
            postDao.insertPost(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun saveWithAttachment(
        post: Post,
        upload: MediaUpload,
        type: AttachmentType,
    ) {
        try {
            val media = uploadWithContent(upload)
            val postWithAttachment =
                post.copy(attachment = Attachment(media.url, type))
            savePost(postWithAttachment)
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

            val response = postApiService.uploadMedia(media)
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
            postDao.removeById(id)
            val response = postApiService.removeById(id)
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
            postDao.likeById(id)
            val response = postApiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())

            postDao.insertPost(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun unlikeById(id: Long) {
        try {
            postDao.unlikeById(id)
            val response = postApiService.unlikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())

            postDao.insertPost(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}