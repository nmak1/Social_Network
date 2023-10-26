package ru.netology.social_network.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.social_network.api.UserApiService
import ru.netology.social_network.dao.UserDao
import ru.netology.social_network.dto.User
import ru.netology.social_network.entity.toDto
import ru.netology.social_network.entity.toUserEntity
import ru.netology.social_network.errors.ApiError
import ru.netology.social_network.errors.NetworkError
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userApiService: UserApiService,
) : UserRepository {

    override val data: Flow<List<User>> =
        userDao.getAll()
            .map { it.toDto() }
            .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            userDao.getAll()
            val response = userApiService.getUsers()
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())
            userDao.insert(body.toUserEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}