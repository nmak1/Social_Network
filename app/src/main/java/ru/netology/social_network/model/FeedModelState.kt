package ru.netology.social_network.model

import ru.netology.social_network.dto.Post
import ru.netology.social_network.until.RetryTypes


data class FeedModelState(
    val refreshing: Boolean = false,
    val error: Boolean = false,
    val loading: Boolean = false,
    val loginError: Boolean = false,
    val registrationError: Boolean = false,
    val retryId: Long = 0,
    val retryType: RetryTypes? = null,
    val retryPost: Post? = null
)