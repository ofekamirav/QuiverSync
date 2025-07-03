//package org.example.quiversync.data.repository
//
//import org.example.quiversync.GeminiMatchQueries
//import org.example.quiversync.data.remote.api.GeminiApi
//import org.example.quiversync.data.session.SessionManager
//import org.example.quiversync.domain.model.GeminiMatch
//import org.example.quiversync.domain.model.Surfboard
//import org.example.quiversync.domain.model.User
//import org.example.quiversync.domain.model.forecast.DailyForecast
//import org.example.quiversync.domain.model.forecast.WeeklyForecast
//import org.example.quiversync.domain.repository.GeminiRepository
//import org.example.quiversync.utils.extensions.toGeminiMatch
//
//class GeminiRepositoryImpl(
//    private val geminiApi: GeminiApi,
//    private val geminiQueries: GeminiMatchQueries,
//) : GeminiRepository {
//
//    override suspend fun generateAndStoreBestBoardMatch(
//        boards: List<Surfboard>,
//        forecast: DailyForecast,
//        user: User
//    ): Result<GeminiMatch> {
//
//        val apiResult = geminiApi.getBoardMatches(boards, forecast, user)
//        if (apiResult.isFailure) return Result.failure(apiResult.exceptionOrNull()!!)
//
//        val matches = apiResult.getOrThrow()
//        val best = matches.maxByOrNull { it.score } ?: return Result.failure(Exception("No matches"))
//
//        geminiQueries.DELETE_BEST_MATCH_FOR_DATE(
//            user.uid,
//            forecast.date,
//            forecast.latitude,
//            forecast.longitude
//        )
//
//        geminiQueries.INSERT_BEST_MATCH(
//            boardId = best.surfboardId,
//            userId = user.uid,
//            forecastDate = forecast.date,
//            forecastLatitude = forecast.latitude,
//            forecastLongitude = forecast.longitude,
//            score = best.score.toLong()
//        )
//
//        return Result.success(
//            GeminiMatch(
//                boardId = best.surfboardId,
//                userId = user.uid,
//                forecastDate = forecast.date,
//                forecastLatitude = forecast.latitude,
//                forecastLongitude = forecast.longitude,
//                score = best.score
//            )
//        )
//    }
//
//
//    override suspend fun generateAndStoreWeeklyBestMatches(
//        boards: List<Surfboard>,
//        weeklyForecast: WeeklyForecast,
//        user: User
//    ): Result<List<GeminiMatch>> {
//
//        val results = mutableListOf<GeminiMatch>()
//
//        weeklyForecast.list.forEach { dayForecast ->
//            val apiResult = geminiApi.getBoardMatches(boards, dayForecast, user)
//            if (apiResult.isFailure) return Result.failure(apiResult.exceptionOrNull()!!)
//
//            val matches = apiResult.getOrThrow()
//            val best = matches.maxByOrNull { it.score } ?: return@forEach
//
//            geminiQueries.DELETE_BEST_MATCH_FOR_DATE(
//                user.uid,
//                dayForecast.date,
//                dayForecast.latitude,
//                dayForecast.longitude
//            )
//
//            geminiQueries.INSERT_BEST_MATCH(
//                boardId = best.surfboardId,
//                userId = user.uid,
//                forecastDate = dayForecast.date,
//                forecastLatitude = dayForecast.latitude,
//                forecastLongitude = dayForecast.longitude,
//                score = best.score.toLong()
//            )
//
//            results.add(
//                GeminiMatch(
//                    boardId = best.surfboardId,
//                    userId = user.uid,
//                    forecastDate = dayForecast.date,
//                    forecastLatitude = dayForecast.latitude,
//                    forecastLongitude = dayForecast.longitude,
//                    score = best.score
//                )
//            )
//        }
//
//        return Result.success(results)
//    }
//
//    override suspend fun getBestBoardMatchForToday(
//        userId: String,
//        date: String,
//        latitude: Double,
//        longitude: Double
//    ): GeminiMatch? {
//        return geminiQueries.GET_TOP_MATCH_BY_USER_SPOT_AND_DATE(
//            userId, date, latitude, longitude
//        ).executeAsOneOrNull()?.toGeminiMatch()
//    }
//
//}
