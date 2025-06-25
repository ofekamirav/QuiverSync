package org.example.quiversync.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File

fun Bitmap.toCompressedByteArray(
    maxSizeKb: Int = 500,
    initialQuality: Int = 90,
    minQuality: Int = 30
): ByteArray {
    var quality = initialQuality
    var stream: ByteArrayOutputStream
    var bytes: ByteArray

    do {
        stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        bytes = stream.toByteArray()
        quality -= 5
    } while (bytes.size / 1024 > maxSizeKb && quality >= minQuality)

    return bytes
}

fun Bitmap.toFile(context: Context, name: String): File {
    val file = File(context.cacheDir, "$name.jpg")
    file.outputStream().use { out ->
        this.compress(Bitmap.CompressFormat.JPEG, 80, out) // 80% quality
    }
    return file
}
