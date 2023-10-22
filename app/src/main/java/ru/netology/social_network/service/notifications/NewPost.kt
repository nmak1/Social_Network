package ru.netology.social_network.service.notifications

data class NewPost(
    val postId: Long,
    val postAuthor: String,
    val postContent: String
)