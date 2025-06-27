package org.example.quiversync.data.remote.cloudinary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CloudinaryUploadResponse(
    @SerialName("secure_url")
    val secureUrl: String,
    @SerialName("public_id")
    val publicId: String
)