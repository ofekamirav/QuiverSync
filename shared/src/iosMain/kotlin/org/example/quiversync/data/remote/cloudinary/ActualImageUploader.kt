package org.example.quiversync.data.remote.cloudinary
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import org.example.quiversync.BuildKonfig
import org.example.quiversync.data.local.Error
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.CloudinaryError

class ActualImageUploader(private val client: HttpClient) : ImageUploader {

    override suspend fun uploadImage(
        bytes: ByteArray,
        fileName: String,
        folder: String
    ): Result<String, Error> {
        val cloudName = BuildKonfig.CLOUD_NAME
        val uploadPreset = BuildKonfig.UPLOAD_PRESET
        val url = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"

        platformLogger("ActualImageUploader(iOS)", "Requesting image upload via Ktor/Darwin to $url")

        return try {
            val response = client.post(url) {
                setBody(MultiPartFormDataContent(
                    formData {
                        append("file", bytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        })
                        append("upload_preset", uploadPreset)
                        append("folder", folder)
                    }
                ))
            }

            if (response.status.isSuccess()) {
                val responseBody = response.body<CloudinaryUploadResponse>()
                platformLogger("ActualImageUploader(iOS)", "Image uploaded successfully: ${responseBody.secureUrl}")
                Result.Success(responseBody.secureUrl)
            } else {
                val errorBody = response.body<String>()
                platformLogger("ActualImageUploader(iOS)", "Image upload failed: ${response.status} - $errorBody")
                Result.Failure(CloudinaryError("Ktor/Darwin upload failed: ${response.status} - $errorBody"))
            }
        } catch (e: Exception) {
            platformLogger("ActualImageUploader(iOS)", "Image upload failed: ${e.message}")
            Result.Failure(CloudinaryError("Ktor/Darwin upload failed: ${e.message ?: "Unknown error"}"))
        }
    }
}