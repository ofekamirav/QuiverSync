package org.example.quiversync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContentResponse
)

@Serializable
data class GeminiContentResponse(
    val parts: List<GeminiPartResponse>
)

@Serializable
data class GeminiPartResponse(
    val text: String
)

@Serializable
data class GeminiMatchResponse(
    val forecastLatitude: Double,//forecast id && spotID
    val forecastLongitude: Double,//forecast id
    val date: String,
    val surfboardID: String,
    val score: Int,
    val description: String
)

data class BoardMatchUi(
    val surfboardId: String,
    val brand: String,
    val model: String?,
    val imageUrl: String?,
    val score: Int
)

