package ru.netology.social_network.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.social_network.dto.Job

@Entity
data class JobEntity(
    @PrimaryKey
    val id: Long = 0L,
    val name: String = "",
    val position: String = "",
    val start: String = "",
    val finish: String? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false,
) {
    fun toDto() = Job(
        id = id,
        name = name,
        position = position,
        start = start,
        finish = finish,
        link = link,
        ownedByMe = ownedByMe,
    )

    companion object {
        fun fromDto(dto: Job) =
            JobEntity(
                dto.id,
                dto.name,
                dto.position,
                dto.start,
                dto.finish,
                dto.link,
                dto.ownedByMe,
            )
    }
}

fun List<JobEntity>.toDto() = map(JobEntity::toDto)
fun List<Job>.toJobEntity() = map(JobEntity::fromDto)
