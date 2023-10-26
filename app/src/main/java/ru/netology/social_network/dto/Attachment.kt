package ru.netology.social_network.dto

import ru.netology.social_network.enumeration.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType,
)
