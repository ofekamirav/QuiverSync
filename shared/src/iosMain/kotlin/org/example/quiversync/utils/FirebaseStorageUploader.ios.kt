package org.example.quiversync.utils

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.darwin.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.skia.Bitmap

actual class FirebaseStorageUploader {

    actual suspend fun uploadImage(
        imageBytes: ByteArray,
        name: String,
        folder: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ): Boolean {
        return true
    }
}
