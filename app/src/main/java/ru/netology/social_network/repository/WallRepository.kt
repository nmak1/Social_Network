package ru.netology.social_network.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.social_network.dto.Post

interface WallRepository {

    fun loadUserWall(userId: Long): Flow<PagingData<Post>>
}