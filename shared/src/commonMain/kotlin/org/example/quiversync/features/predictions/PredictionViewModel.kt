package org.example.quiversync.features.predictions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.Prediction.DailyPrediction
import org.example.quiversync.domain.model.Prediction.WeeklyPrediction
import org.example.quiversync.domain.model.Surfboard
import kotlinx.coroutines.launch
import org.example.quiversync.features.BaseViewModel

class PredictionViewModel:BaseViewModel() {
    private val _uiState = MutableStateFlow<PredictionState>(PredictionState.Loading)
    val uiState: StateFlow<PredictionState> get() = _uiState

    init {
        fetchPrediction()
    }
    private fun fetchPrediction() {
        scope.launch {
            val prediction = createMockPrediction()
            _uiState.emit(
                PredictionState.Loaded(prediction)
            )
        }
    }
}

private fun createMockPrediction(): WeeklyPrediction {
    val boards = listOf(
        Surfboard(
            id = "1",
            ownerId = "",
            model = "Holly Grail",
            company = "Hayden Shapes",
            type = "Shortboard",
            imageRes = "hs_shortboard",
            height = "6'2\"",
            volume = "32L",
            width = "19\"",
            addedDate = "2024-05-01",
            isRentalPublished = false,
            isRentalAvailable = false
        ),
        Surfboard(
            id = "2",
            ownerId = "",
            model = "FRK+",
            company = "Slater Designs",
            type = "Funboard",
            imageRes = "hs_shortboard",
            height = "5'8\"",
            volume = "28L",
            width = "20 1/4\"",
            addedDate = "2024-04-15",
            isRentalPublished = true,
            isRentalAvailable = true,
            pricePerDay = 10.0
        )
    )

    val forecasts = listOf(
        DailyForecast("2025-06-21", 0.63, 3.37, 248.61, 5.89, 294.67 , 23.2 ,23.2),
        DailyForecast("2025-06-22", 0.77, 3.54, 227.29, 6.33, 293.84 , 23.2,23.2),
        DailyForecast("2025-06-23", 1.04, 3.06, 226.87, 7.34, 294.53,23.2,23.2),
        DailyForecast("2025-06-24", 0.81, 2.83, 229.92, 7.34, 298.98,23.2,23.2),
        DailyForecast("2025-06-25", 0.58, 2.84, 226.47, 6.33, 298.79,23.2,23.2),
        DailyForecast("2025-06-26", 0.47, 3.06, 245.2, 6.0, 297.6,23.2,23.2)
    )

    val predictions = forecasts.mapIndexed { index, forecast ->
        DailyPrediction(
            DailyForecast = forecast,
            Surfboard = boards[index % boards.size], // Alternate boards
            confidence = 50 + (index * 8) % 50 // Fake confidence between 50–98
        )
    }

    return WeeklyPrediction(List = predictions)
}
