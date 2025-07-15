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
        return Result.Success("IOS uploader is been activated")
    }
}
