package ru.netology.social_network.service.notifications

data class Push(
    val content: String,
    val recipientId: Long?,
)