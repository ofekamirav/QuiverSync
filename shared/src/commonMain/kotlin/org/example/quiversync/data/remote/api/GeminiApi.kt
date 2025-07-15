package org.example.quiversync.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import org.example.quiversync.BuildKonfig
import org.example.quiversync.data.local.Result
import org.example.quiversync.data.remote.dto.GeminiContent
import org.example.quiversync.data.remote.dto.GeminiMatchResponse
import org.example.quiversync.data.remote.dto.GeminiPart
import org.example.quiversync.data.remote.dto.GeminiRequest
import org.example.quiversync.data.remote.dto.GeminiResponse
import org.example.quiversync.data.repository.TMDBError
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.model.forecast.DailyForecast

class GeminiApi(
    private val httpClient: HttpClient
) {

    private fun buildPrompt(
        boards: List<Surfboard>,
        forecast: DailyForecast,
        user: User
    ): String {
        val boardDescriptions = boards.mapIndexed { index, b ->
            "${index + 1}. ID: ${b.id}, ${b.company} ${b.model ?: "–"} (${b.type}) – ${b.height}ft x ${b.width ?: "?"}in, ${b.volume ?: "?"}L"
        }.joinToString("\n")

        return """
        You are a world-class surfboard-matching expert. Your expertise includes surfboard design, wave dynamics, and surfer physiology.

        A surfer is planning their next session. Their profile:
        - Height: ${user.heightCm} cm
        - Weight: ${user.weightKg} kg
        - Skill Level: ${user.surfLevel}

        Surf forecast:
        - Date: ${forecast.date}
        - Location: Latitude ${forecast.latitude}, Longitude ${forecast.longitude}
        - Wave Height: ${forecast.waveHeight} m
        - Swell Period: ${forecast.swellPeriod} s
        - Swell Direction: ${forecast.swellDirection}°
        - Wind Speed: ${forecast.windSpeed} m/s
        - Wind Direction: ${forecast.windDirection}°

        Available surfboards:
        $boardDescriptions

        --- TASK ---

        Rate the compatibility of **each board** with the surfer and the forecasted conditions.

        Prioritize:
        🌊 Wave Fit
        - Wave height and swell period are critical.
        - Consider paddle speed, early entry, stability, maneuverability.
        - Adjust for wind direction and speed.

        🧠 Surfer Fit
        - "Kook" = beginner: needs volume & stability.
        - "Intermediate": can ride more responsive boards.
        - "Pro": can handle any board, match to wave dynamics.

        🧮 Scoring:
        - Scale: 0–100
        - 90–100 = ideal, 70–89 = good, 50–69 = usable, <50 = mismatch
        - Give concise reasoning (max 30 words).

        --- OUTPUT FORMAT (strictly JSON, one array of objects) ---

        Respond **only** with a valid JSON array. Do not wrap it in an outer object .

        Each item in the array must be:

        {
          "surfboardID": "<board ID>",
          "score": 85,
          "description": "Short explanation (max 30 words)",
          "forecastLatitude": ${forecast.latitude},
          "forecastLongitude": ${forecast.longitude},
          "date": "${forecast.date}"
        }
        
        Do not include any explanation or markdown formatting. Respond with the raw JSON array only.

        """.trimIndent()
    }

    private fun buildPromptForWeek(
        boards: List<Surfboard>,
        forecastWeek: List<DailyForecast>,
        user: User
    ): String {
        val boardDescriptions = boards.mapIndexed { index, b ->
            "${index + 1}. ID: ${b.id}, ${b.company} ${b.model ?: "–"} (${b.type}) – ${b.height}ft x ${b.width ?: "?"}in, ${b.volume ?: "?"}L"
        }.joinToString("\n")

        val forecastDescriptions = forecastWeek.joinToString("\n") { f ->
            "- Date: ${f.date} | Wave: ${f.waveHeight}m @ ${f.swellPeriod}s | Swell Dir: ${f.swellDirection}° | Wind: ${f.windSpeed}m/s ${f.windDirection}° | Location: (${f.latitude}, ${f.longitude})"
        }

        return """
        You are a world-class surfboard recommendation system. Your expertise includes surfboard design, wave dynamics, and surfer physiology.

        A surfer is preparing for the upcoming 6 days and needs the best surfboard for each day.

        --- SURFER PROFILE ---
        - Height: ${user.heightCm} cm
        - Weight: ${user.weightKg} kg
        - Skill Level: ${user.surfLevel}

        --- AVAILABLE SURFBOARDS ---
        $boardDescriptions

        --- 6-DAY SURF FORECAST ---
        $forecastDescriptions

        --- TASK ---

        For **each day**, evaluate **all boards**, and select the **single best board** for that day.

        Analyze based on:
        • Wave height and swell period
        • Wind direction and speed
        • Board volume, shape, and type
        • Surfer skill level and required stability/performance

        --- SCORING ---
        • Use a 0–100 score to rate the chosen board per day
        • 90–100 = excellent match, 70–89 = good, 50–69 = usable, <50 = mismatch
        • Justify each choice clearly with a **unique** explanation (max 30 words)

        --- OUTPUT FORMAT (STRICTLY JSON ARRAY, ONE OBJECT PER DAY) ---

        Return only a **JSON array** with **6 objects** — one per forecast day.

        Each object must follow this format exactly:

        {
            "surfboardID": "<board ID>",
            "score": <integer between 0 and 100>,
            "description": "Unique explanation for the choice, max 30 words",
            "forecastLatitude": <latitude>,
            "forecastLongitude": <longitude>,
            "date": "<YYYY-MM-DD>"
        }

        ⚠️ Guidelines:
        - Only 6 predictions total (1 per day)
        - No duplicate boards unless truly the best fit again
        - No repeated explanations
        - Be accurate and concise
        - Output must be strictly valid JSON array — no text before or after
    """.trimIndent()
    }



    private fun buildPromptForMultipleSpots(
        boards: List<Surfboard>,
        forecasts: List<DailyForecast>,
        user: User
    ): String {
        val boardDescriptions = boards.mapIndexed { index, b ->
            "${index + 1}. ID: ${b.id}, ${b.company} ${b.model ?: "–"} (${b.type}) – ${b.height}ft x ${b.width ?: "?"}in, ${b.volume ?: "?"}L"
        }.joinToString("\n")

        val forecastDescriptions = forecasts.joinToString("\n") { f ->
            "- Date: ${f.date} | Location: (${f.latitude}, ${f.longitude}) | Wave: ${f.waveHeight}m @ ${f.swellPeriod}s | Swell Dir: ${f.swellDirection}° | Wind: ${f.windSpeed}m/s ${f.windDirection}°"
        }

        return """
        You are a world-class surfboard recommendation system. Your job is to evaluate surfboards based on forecasted wave conditions and a surfer's physical profile and skill level.

        --- SURFER PROFILE ---
        - Height: ${user.heightCm} cm
        - Weight: ${user.weightKg} kg
        - Skill Level: ${user.surfLevel}

        --- AVAILABLE SURFBOARDS ---
        $boardDescriptions

        --- FORECASTED SESSIONS ---
        Below are forecasts for different locations and dates:

        $forecastDescriptions

        --- TASK ---

        For **each forecasted session**, evaluate all boards and return the **best-matched board**.

        Take into account:
        • Wave height and swell period
        • Wind direction and speed
        • Board volume, type, and shape
        • Surfer’s level and physical attributes

        Use professional logic:
        - Prioritize paddle power and early entry on small/mushy waves
        - Prioritize control and responsiveness in strong swell
        - Match board responsiveness to surfer level:
            - “Kook” = high volume & stability
            - “Intermediate” = mid-volume with responsiveness
            - “Pro” = match board type to wave quality

        --- SCORING ---
        • Give a score (0–100) for the best-matched board per forecast
        • 90–100 = perfect fit, 70–89 = good, 50–69 = usable, <50 = mismatch
        • Explanation must be unique and tied to that forecast

        --- OUTPUT FORMAT (JSON ARRAY, ONE ITEM PER FORECAST) ---
        Return a valid JSON array like the example below — **strictly one object per forecast** (not per board):

        [
          {
            "surfboardID": "<best board ID>",
            "score": 88,
            "description": "Handles waist-high waves and light wind well for an intermediate surfer.",
            "forecastLatitude": <latitude>,
            "forecastLongitude": <longitude>,
            "date": "<YYYY-MM-DD>"
          },
          ...
        ]

        ⚠️ Rules:
        - Only one prediction per forecast
        - No duplicate objects for the same date+location
        - No repeated explanations
        - Output must be strictly valid JSON — no extra text
    """.trimIndent()
    }




    suspend fun predictionForOneDay(
        boards: List<Surfboard>,
        forecast: DailyForecast,
        user: User
    ): Result<GeminiPrediction, TMDBError> {
        return try {
            val prompt = buildPrompt(boards, forecast, user)

            val requestBody = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(text = prompt)
                        )
                    )
                )
            )

            println("GeminiApi: Sending request to Gemini API")

            val response = httpClient.post("https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=${BuildKonfig.GEMINI_API_KEY}") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status == HttpStatusCode.OK) {
                val geminiResponse = response.body<GeminiResponse>()

                val fullText = geminiResponse.candidates.first().content.parts.first().text

                val jsonText = Regex("""\{[\s\S]*?\}""")
                    .find(fullText)
                    ?.value
                    ?: throw IllegalStateException("No JSON object found in Gemini response")

                println("✅ GeminiApi: Extracted JSON text: $jsonText")

                val matches: GeminiMatchResponse = Json.decodeFromString(jsonText)



                Result.Success(
                    GeminiPrediction(
                        predictionID = 0, // Assuming ID is auto-generated
                        userID = user.uid,
                        surfboardID = matches.surfboardID,
                        score = matches.score,
                        description = matches.description,
                        forecastLatitude = matches.forecastLatitude,
                        forecastLongitude = matches.forecastLongitude,
                        date = matches.date
                    )
                )
            } else {
                val errorBody = response.bodyAsText()
                println("❌ GeminiApi Error: ${response.status}")
                println("❌ Error body: $errorBody")
                Result.Failure(TMDBError("Gemini API error: ${response.status.value}"))
            }

        } catch (e: Exception) {
            println("GeminiApi: Exception in predictionForOneDay: ${e.message}")
            e.printStackTrace()
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }

    suspend fun predictionForWeek(
        boards: List<Surfboard>,
        forecast: List<DailyForecast>,
        user: User
    ): Result<List<GeminiPrediction>, TMDBError> {
        return try {
            val prompt = buildPromptForWeek(boards, forecast, user)

            val requestBody = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(text = prompt)
                        )
                    )
                )
            )

            println("GeminiApi: Sending request to Gemini API")

            val response = httpClient.post("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent?key=${BuildKonfig.GEMINI_API_KEY}") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status == HttpStatusCode.OK) {
                val geminiResponse = response.body<GeminiResponse>()

                val fullText = geminiResponse.candidates.first().content.parts.first().text

                val jsonArrayText = Regex("""\[[\s\S]*?\]""")
                    .find(fullText)
                    ?.value
                    ?: throw IllegalStateException("No JSON array found in Gemini response")

                println("✅ GeminiApi: Extracted JSON array: $jsonArrayText")

                val matchesList: List<GeminiMatchResponse> = Json.decodeFromString(jsonArrayText)

                // Convert each match to a GeminiPrediction
                val predictions = matchesList.map { match ->
                    GeminiPrediction(
                        predictionID = 0, // Assuming ID is auto-generated
                        userID = user.uid,
                        surfboardID = match.surfboardID,
                        score = match.score,
                        description = match.description,
                        forecastLatitude = match.forecastLatitude,
                        forecastLongitude = match.forecastLongitude,
                        date = match.date
                    )
                }

                Result.Success(predictions)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ GeminiApi Error: ${response.status}")
                println("❌ Error body: $errorBody")
                Result.Failure(TMDBError("Gemini API error: ${response.status.value}"))
            }

        } catch (e: Exception) {
            println("GeminiApi: Exception in predictionForWeek: ${e.message}")
            e.printStackTrace()
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }

    suspend fun predictionForMultipleSpots(
        boards: List<Surfboard>,
        forecasts: List<DailyForecast>,
        user: User
    ): Result<List<GeminiPrediction>, TMDBError> {
        return try {
            val prompt = buildPromptForMultipleSpots(boards, forecasts, user)

            val requestBody = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(text = prompt)
                        )
                    )
                )
            )

            println("GeminiApi: Sending request to Gemini API")

            val response =
                httpClient.post("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent?key=${BuildKonfig.GEMINI_API_KEY}") {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }

            if (response.status == HttpStatusCode.OK) {
                val geminiResponse = response.body<GeminiResponse>()

                val fullText = geminiResponse.candidates.first().content.parts.first().text

                val jsonArrayText = Regex("""\[[\s\S]*?\]""")
                    .find(fullText)
                    ?.value
                    ?: throw IllegalStateException("No JSON array found in Gemini response")

                println("✅ GeminiApi: Extracted JSON array: $jsonArrayText")

                val matchesList: List<GeminiMatchResponse> = Json.decodeFromString(jsonArrayText)

                // Convert each match to a GeminiPrediction
                val predictions = matchesList.map { match ->
                    GeminiPrediction(
                        predictionID = 0, // Assuming ID is auto-generated
                        userID = user.uid,
                        surfboardID = match.surfboardID,
                        score = match.score,
                        description = match.description,
                        forecastLatitude = match.forecastLatitude,
                        forecastLongitude = match.forecastLongitude,
                        date = match.date
                    )
                }

                Result.Success(predictions)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ GeminiApi Error: ${response.status}")
                println("❌ Error body: $errorBody")
                Result.Failure(TMDBError("Gemini API error: ${response.status.value}"))
            }

        } catch (e: Exception) {
            println("GeminiApi: Exception in predictionForMultipleSpots: ${e.message}")
            e.printStackTrace()
            Result.Failure(TMDBError(e.message ?: "Unknown error"))
        }
    }



}




