package org.example.quiversync.utils.extensions

import org.example.quiversync.GeminiMatchEntity
import org.example.quiversync.domain.model.GeminiMatch

fun GeminiMatchEntity.toGeminiMatch(): GeminiMatch {
    return GeminiMatch(
        boardId = boardId,
        userId = userId,
        forecastDate = forecastDate,
        forecastLatitude = forecastLatitude,
        forecastLongitude = forecastLongitude,
        score = score.toInt()
    )
}