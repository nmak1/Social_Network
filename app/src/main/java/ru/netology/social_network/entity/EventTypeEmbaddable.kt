package ru.netology.social_network.entity

import ru.netology.social_network.enumeration.EventType

data class EventTypeEmbeddable(
    val eventType: String,
) {
    fun toDto() = EventType.valueOf(eventType)

    companion object {
        fun fromDto(dto: EventType) = EventTypeEmbeddable(dto.name)
    }
}