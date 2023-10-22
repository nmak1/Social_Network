package ru.netology.social_network.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.social_network.dto.Attachment
import ru.netology.social_network.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorId: Long,
    val published: String,
    val content: String,
    val likeByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Boolean = false,
    val videoUrl: String? = null,
    val authorAvatar: String? = null,
    @Embedded
    val attachment: Attachment?,
    val ownedByMe: Boolean,
) {
    fun toDto() =
        Post(
            id, author, authorId, published, content, likeByMe, likes, shares, views, videoUrl, authorAvatar,attachment?.toDto(),
            ownedByMe)

    companion object {
        fun fromDto(dto: Post) = with(dto) {
            PostEntity(
                dto.id,
                dto.author,
                dto.authorId,
                dto.published,
                dto.content,
                dto.likedByMe,
                dto.likes,
                dto.shares,
                dto.views,
                dto.videoUrl,
                dto.authorAvatar,
                Attachment.fromDto(dto.attachment),
                dto.ownedByMe
            )
        }

    }
}

fun List<PostEntity>.toDto() = map { it.toDto() }
fun List<Post>.toEntity() = map { PostEntity.fromDto(it) }
