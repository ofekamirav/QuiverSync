package org.example.quiversync.domain.usecase

import kotlinx.datetime.Clock
import org.example.quiversync.data.remote.cloudinary.ImageUploader

class UploadImageUseCase( private val imageUploader: ImageUploader) {
    suspend operator fun invoke(bytes: ByteArray, folder: Folder): Result<String> {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val uniqueFileName = "${folder.folderName}_${timestamp}.jpg"
        return imageUploader.uploadImage(
            bytes = bytes,
            fileName = uniqueFileName,
            folder = folder.folderName
        )
    }

    enum class Folder(val folderName: String) {
        PROFILES("user_profiles"),
        SURFBOARDS("surfboards")
    }
}