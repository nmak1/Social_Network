package ru.netology.social_network.model

import ru.netology.social_network.dto.Job

data class JobModel(
    val jobs: List<Job> = emptyList(),
    val empty: Boolean = false,
)