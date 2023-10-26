package ru.netology.social_network.model

import android.net.Uri
import ru.netology.social_network.enumeration.AttachmentType
import java.io.InputStream

data class MediaModel(
    val uri: Uri? = null,
    val inputStream: InputStream? = null,
    val type: AttachmentType? = null,
)