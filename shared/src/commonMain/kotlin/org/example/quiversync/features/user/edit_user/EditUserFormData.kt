package org.example.quiversync.features.user.edit_user


data class EditUserFormData(
    val name: String? = null,
    val nameError: String? = null,
    val height: Double? = null,
    val heightError: String? = null,
    val weight: Double? = null,
    val weightError: String? = null,
    val surfLevel: String? = null,
    val profilePicture: String? = null,
    val profilePictureError: String? = null,
    val isUploadingImage: Boolean = false,
    val imageUploadError: String? = null,
)