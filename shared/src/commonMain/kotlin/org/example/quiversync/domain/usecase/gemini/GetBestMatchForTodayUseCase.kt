//package org.example.quiversync.domain.usecase.gemini
//
//import org.example.quiversync.data.local.Result
//import org.example.quiversync.data.repository.TMDBError
//import org.example.quiversync.domain.model.FavoriteSpot
//import org.example.quiversync.domain.model.prediction.GeminiPrediction
//import org.example.quiversync.domain.repository.GeminiRepository
//
//class GetBestMatchForTodayUseCase(
//    private val geminiRepository: GeminiRepository
//) {
//    suspend operator fun invoke(
//        spot : FavoriteSpot
//    ):Result<GeminiPrediction,TMDBError>{
//        return geminiRepository.getBestBoardMatchForToday(spot)
//    }
//}