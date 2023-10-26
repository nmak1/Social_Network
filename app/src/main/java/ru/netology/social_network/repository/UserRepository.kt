package ru.netology.social_network.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.social_network.dto.User

interface UserRepository {

    val data: Flow<List<User>>

    suspend fun getAll()
}