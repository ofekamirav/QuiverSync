package org.example.quiversync.data.remote.cloudinary

import android.content.Context
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import org.example.quiversync.BuildKonfig
import org.example.quiversync.utils.extensions.platformLogger
import java.io.File
import kotlin.coroutines.resume

class ActualImageUploader(private val context: Context) : ImageUploader {

    override suspend fun uploadImage(bytes: ByteArray, fileName: String, folder: String): Result<String> {
        val tempFile = File(context.cacheDir, fileName).apply {
            writeBytes(bytes)
        }

        return suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(tempFile.path)
                .unsigned(BuildKonfig.UPLOAD_PRESET)
                .option("folder", folder)
                .callback(object : UploadCallback {
                    override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                        val url = resultData?.get("secure_url") as? String
                        if (url != null) {
                            tempFile.delete()
                            platformLogger("ActualImageUploader-android", "Image uploaded successfully: $url")
                            continuation.resume(Result.success(url))
                        } else {
                            tempFile.delete()
                            platformLogger("ActualImageUploader-android", "Cloudinary upload failed: URL is null")
                            continuation.resume(Result.failure(Exception("Cloudinary upload failed: URL is null")))
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        tempFile.delete()
                        continuation.resume(Result.failure(Exception("Cloudinary error: ${error?.description}")))
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                })
                .dispatch()

            continuation.invokeOnCancellation {
                MediaManager.get().cancelRequest(continuation.toString())
            }
        }
    }
}