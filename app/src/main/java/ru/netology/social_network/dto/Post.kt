package ru.netology.social_network.dto

sealed interface FeedItem {
    val id: Long
}

data class Post(
    override val id: Long,
    val author: String,
    val authorId: Long,
    val published: String,
    val content: String,
    val likedByMe: Boolean ,
    val likes: Int =0 ,
    val shares: Int =0 ,
    val views: Boolean =false ,
    val videoUrl: String?= null ,
    val authorAvatar: String?=null,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
) : FeedItem

data class Ad(
    override val id: Long,
    val image: String,
) : FeedItem


