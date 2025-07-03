package org.example.quiversync.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import org.example.quiversync.data.remote.dto.GeminiContent
import org.example.quiversync.data.remote.dto.GeminiMatchResponse
import org.example.quiversync.data.remote.dto.GeminiPart
import org.example.quiversync.data.remote.dto.GeminiRequest
import org.example.quiversync.data.remote.dto.GeminiResponse
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast

class GeminiApi(
    private val httpClient: HttpClient,
    //private val geminiBaseUrl: String,
    //private val geminiApiKey: String
) {

    fun buildPrompt(
        boards: List<Surfboard>,
        forecast: DailyForecast,
        user: User
    ): String {
        val boardDescriptions = boards.mapIndexed { index, b ->
            "${index + 1}. ${b.company} ${b.model ?: "–"} (${b.type}) – ${b.height}ft x ${b.width ?: "?"}in, ${b.volume ?: "?"}L}"
        }.joinToString("\n")

        return """
            You are a world-class surfboard-matching expert with deep knowledge of surfboard design, wave dynamics, and surfer physiology.

            A surfer is planning their next session. Here are their attributes:
            - Height: ${user.heightCm} cm
            - Weight: ${user.weightKg} kg
            - Surfing Level: ${user.surfLevel}

            Surf forecast:
            - Wave Height: ${forecast.waveHeight} meters
            - Swell Period: ${forecast.swellPeriod} seconds
            - Swell Direction: ${forecast.swellDirection}°
            - Wind Speed: ${forecast.windSpeed} m/s
            - Wind Direction: ${forecast.windDirection}°

            They have the following boards:

            $boardDescriptions

            Your task is to rate how compatible each board is for the *given surf conditions* and the *surfer's profile*. Take into account:

            ### 🌊 Wave conditions
            - Wave height and swell period are the most important.
            - Consider if the board offers enough paddle speed, early entry, control, and maneuverability.
            - Adjust based on wind direction/speed.

            ### 🧠 Surfer Level Logic
            - "Kook" = beginner: needs high volume & stability.
            - "Intermediate": can handle more responsive boards.
            - "Pro": can ride anything, match boards to wave type.

            ### 🧮 Scoring Logic
            - Score from 0–100.
            - 90+ = ideal match, 70–89 = decent match, 50–69 = usable, below 50 = mismatch.
            - Explain **why**.

            Respond with JSON array:
            [
                {
                    "surfboardId": "<board ID>",
                    "score": 85,
                    "reason": "Explanation..."
                }
            ]
        """.trimIndent()
    }

//    suspend fun getBoardMatches(
//        boards: List<Surfboard>,
//        forecast: DailyForecast,
//        user: User
//    ): Result<List<GeminiMatchResponse>> {
//        val prompt = buildPrompt(boards, forecast, user)
//
////        val response: HttpResponse = httpClient.post(geminiBaseUrl) {
////            url {
////                parameters.append("key", geminiApiKey)
////            }
////            contentType(ContentType.Application.Json)
////            setBody(
////                GeminiRequest(
////                    contents = listOf(
////                        GeminiContent(
////                            parts = listOf(GeminiPart(text = prompt))
////                        )
////                    )
////                )
////            )
////        }
//
////        if (response.status.isSuccess()) {
////            val geminiResponse: GeminiResponse = response.body()
////            val jsonText = geminiResponse.candidates.firstOrNull()
////                ?.content?.parts?.firstOrNull()?.text ?: return Result.failure(
////                Exception("No text response from Gemini.")
////            )
//
//            //val matches = Json.decodeFromString<List<GeminiMatchResponse>>(jsonText)
//            return Result.success(matches)
////        } else {
////            return Result.failure(Exception("Gemini request failed: ${response.status}"))
////        }
//    }
}
