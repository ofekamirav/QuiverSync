package org.example.quiversync.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import org.example.quiversync.BuildKonfig
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.GeminiMatchResponse
import org.example.quiversync.data.remote.dto.GeminiResponse
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.Prediction.GeminiPrediction
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
            - Date: ${forecast.date}
            - Wave Height: ${forecast.waveHeight} meters
            - Swell Period: ${forecast.swellPeriod} seconds
            - Swell Direction: ${forecast.swellDirection}°
            - Wind Speed: ${forecast.windSpeed} m/s
            - Wind Direction: ${forecast.windDirection}°
            
            its in the location of :
            - Latitude: ${forecast.latitude}
            - Longitude: ${forecast.longitude}

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

            Respond with JSON Object for the board with the highest score from the board list:
            [
                {
                    "surfboardID": "<board ID>",
                    "score": 85,
                    "description": "Explanation..."
                    "forecastLatitude" : "${forecast.latitude} 
                    "forecastLongitude" : ${forecast.longitude}"
                    "date" : "${forecast.date}"
                }
            ]
        """.trimIndent()
    }

    suspend fun predictionForOneDay(
        boards: List<Surfboard>,
        forecast: DailyForecast,
        user: User
    ): Result<GeminiPrediction, TMDBError> {
        try {
            val prompt = buildPrompt(boards, forecast, user)
            val response = httpClient.get("geminiBaseUrl") {
                header(
                    HttpHeaders.Authorization,
                    BuildKonfig.STORM_GLASS_API_KEY
                ) // Will use Gemini API key
                parameter("prompt", prompt)
            }.body<GeminiResponse>()

            val jsonText = response.candidates.first().content.parts.first().text
            val matches: List<GeminiMatchResponse> = Json.decodeFromString(jsonText)
            val bestMatch = matches.first()

            return Result.Success(
                GeminiPrediction(
                    surfboardID = bestMatch.surfboardID,
                    score = bestMatch.score,
                    description = bestMatch.description,
                    forecastLatitude = bestMatch.forecastLatitude,
                    forecastLongitude = bestMatch.forecastLongitude,
                    forecastDate = bestMatch.forecastDate
                )
            )

        } catch (e: Exception) {
            return Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }

    suspend fun predictionForWeek(
        boards: List<Surfboard>,
        forecast: List<DailyForecast>,
        user: User
    ): Result<List<GeminiPrediction>, TMDBError> {
        try {
            val predictions = mutableListOf<GeminiPrediction>()
            for (dayForecast in forecast) {
                val result = predictionForOneDay(boards, dayForecast, user)
                if (result is Result.Failure) {
                    return Result.Failure(result.error)
                }
                val bestMatch = (result as Result.Success).data
                if (bestMatch != null) {
                    predictions.add(
                        GeminiPrediction(
                            surfboardID = bestMatch.surfboardID,
                            score = bestMatch.score,
                            description = bestMatch.description,
                            forecastLatitude = bestMatch.forecastLatitude,
                            forecastLongitude = bestMatch.forecastLongitude,
                            forecastDate = bestMatch.forecastDate
                        )
                    )
                }
            }
            return Result.Success(predictions)
        } catch (e: Exception) {
            return Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }
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

