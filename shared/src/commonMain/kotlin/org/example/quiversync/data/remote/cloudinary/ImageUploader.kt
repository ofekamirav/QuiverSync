package org.example.quiversync.data.remote.cloudinary

import org.example.quiversync.data.local.Error
import org.example.quiversync.data.local.Result

interface ImageUploader {
    suspend fun uploadImage(
        bytes: ByteArray,
        fileName: String,
        folder: String
    ): Result<String, Error>
}