package org.example.quiversync.utils

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

actual class FirebaseStorageUploader {
    private val storage: FirebaseStorage = Firebase.storage

    actual suspend fun uploadImage(
        imageBytes: ByteArray,
        name: String,
        folder: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ): Boolean {
        return try {
            val sizeKb = imageBytes.size / 1024
            Log.d("ImageCompression", "Final size: ${sizeKb}KB")

            val storageRef = storage.reference.child("$folder/$name")
            storageRef.putBytes(imageBytes).await()

            val url = storageRef.downloadUrl.await().toString()
            Log.d("FirebaseStorage", "Image uploaded to: $url")
            onSuccess(url)
            true
        } catch (e: Exception) {
            Log.e("FirebaseStorage", "Upload failed: ${e.message}", e)
            onError(e.message ?: "Unknown error")
            false
        }
    }
}