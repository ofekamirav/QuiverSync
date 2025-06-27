package org.example.quiversync.utils.extentions

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun Bitmap.toCompressedByteArray(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 80
): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(format, quality, stream)
    return stream.toByteArray()
}
