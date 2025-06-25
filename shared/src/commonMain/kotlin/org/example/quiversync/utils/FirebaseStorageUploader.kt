package org.example.quiversync.utils


expect class FirebaseStorageUploader {
    suspend fun uploadImage(
        imageBytes: ByteArray,
        name: String,
        folder: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ): Boolean
}
