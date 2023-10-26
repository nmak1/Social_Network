package ru.netology.social_network.dto

data class PushMessage(
    val recipientId: Long?,
    val content: String,
)