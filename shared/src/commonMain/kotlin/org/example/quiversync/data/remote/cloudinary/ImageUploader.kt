package org.example.quiversync.data.remote.cloudinary

interface ImageUploader {
    suspend fun uploadImage(
        bytes: ByteArray,
        fileName: String,
        folder: String
    ): Result<String>
}