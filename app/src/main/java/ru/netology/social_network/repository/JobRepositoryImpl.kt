package ru.netology.social_network.repository

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.social_network.api.JobApiService
import ru.netology.social_network.dao.JobDao
import ru.netology.social_network.dto.Job
import ru.netology.social_network.entity.JobEntity
import ru.netology.social_network.entity.toDto
import ru.netology.social_network.entity.toJobEntity
import ru.netology.social_network.errors.ApiError
import ru.netology.social_network.errors.NetworkError
import java.io.IOException
import javax.inject.Inject


class JobRepositoryImpl @Inject constructor(

    private val jobDao: JobDao,
    private val jobApiService: JobApiService
) : JobRepository {

    override val data: Flow<List<Job>> = jobDao.getJob()
        .map { it.toDto() }
        .flowOn(Dispatchers.Default)

    private val _data = MutableLiveData<List<Job>>()

    override suspend fun saveJob(job: Job) {
        try {
//            jobDao.saveJob(JobEntity.fromDto(job))
            val response = jobApiService.saveJob(job)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())
            jobDao.insertJob(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun getJobByUserId(id: Long) {
        try {
            jobDao.deleteAll()
            val response = jobApiService.getJobByUserId(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())
            _data.postValue(body)
            jobDao.insertJobs(body.toJobEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = jobApiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            jobDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}