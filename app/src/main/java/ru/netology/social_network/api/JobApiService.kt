package ru.netology.social_network.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.social_network.dto.Job

interface JobApiService {

    @GET("{id}/jobs")
    suspend fun getJobByUserId(@Path("id") id: Long): Response<List<Job>>

    @POST("my/jobs")
    suspend fun saveJob(@Body job: Job): Response<Job>

    @DELETE("my/jobs/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>
}