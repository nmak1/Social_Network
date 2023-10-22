package ru.netology.social_network.model

import ru.netology.social_network.dto.Post


data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false
)