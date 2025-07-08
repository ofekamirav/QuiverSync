//package org.example.quiversync.features.predictions
//
//import org.example.quiversync.domain.model.prediction.WeeklyPrediction
//
//sealed class PredictionState {
//    object Loading : PredictionState()
//    data class Loaded(val prediction: WeeklyPrediction) : PredictionState()
//    data class Error(val message: String) : PredictionState()
//
//}