package ru.netology.social_network.model

import android.net.Uri
import java.io.File

data class PhotoModel(
    val uri: Uri? = null,
    val file: File? = null,
)