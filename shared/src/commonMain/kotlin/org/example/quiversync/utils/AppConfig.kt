package org.example.quiversync.utils

import org.example.quiversync.BuildKonfig

object AppConfig {
    val cloudName: String get() = BuildKonfig.CLOUD_NAME
    val uploadPreset: String get() = BuildKonfig.UPLOAD_PRESET
    val googleMapsApiKey: String get() = BuildKonfig.MAPS_API_KRY
}
