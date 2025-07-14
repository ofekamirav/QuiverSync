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
import kotlinx.serialization.json.Json

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
        platformLogger("ActualImageUploader(iOS)", "Uploading file: $fileName in folder: $folder with upload preset: $uploadPreset")

        return try {
            val response = client.post(url) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("upload_preset", uploadPreset.trim(), Headers.build {
                                append(HttpHeaders.ContentType, "text/plain; charset=UTF-8")
                            })

                            append("folder", folder.trim(), Headers.build {
                                append(HttpHeaders.ContentType, "text/plain; charset=UTF-8")
                            })
                            append("file", bytes, Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                                append(HttpHeaders.ContentType, "image/jpeg")
                            })
                        }
                    )
                )
            }

            // --- Start of the corrected logic ---

            // 1. Read the body ONCE as a string
            val responseText = response.body<String>()

            platformLogger("ActualImageUploader(iOS)", "Response status: ${response.status}")
            platformLogger("ActualImageUploader(iOS)", "Response body: $responseText")

            if (response.status.isSuccess()) {
                // 2. Parse the string you already have
                val responseBody = Json.decodeFromString<CloudinaryUploadResponse>(responseText)
                platformLogger("ActualImageUploader(iOS)", "✅ Upload successful: ${responseBody.secureUrl}")
                Result.Success(responseBody.secureUrl)
            } else {
                platformLogger("ActualImageUploader(iOS)", "❌ Upload failed: ${response.status} - $responseText")
                // You can optionally try to parse an error JSON from responseText here if Cloudinary sends one
                Result.Failure(CloudinaryError("Ktor/Darwin upload failed: ${response.status} - $responseText"))
            }
            // --- End of the corrected logic ---

        } catch (e: Exception) {
            platformLogger("ActualImageUploader(iOS)", "❌ Exception during upload: ${e.message}")
            Result.Failure(CloudinaryError("Ktor/Darwin upload failed: ${e.message ?: "Unknown error"}"))
        }
    }
}
