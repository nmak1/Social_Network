package ru.netology.social_network.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.social_network.dto.FeedItem
import ru.netology.social_network.dto.Media
import ru.netology.social_network.dto.MediaUpload
import ru.netology.social_network.dto.Post

interface PostRepository {

    val data: Flow<PagingData<FeedItem>>
    fun getNewerCount(firstId: Long): Flow<Int>
    suspend fun getNewPosts()
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun uploadWithContent(upload: MediaUpload): Media
    suspend fun getAll()
    suspend fun likeById(id: Long)
    suspend fun unlikeById(id: Long)
    suspend fun removeById(id: Long)
}