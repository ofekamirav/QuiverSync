package org.example.quiversync.features.forecast

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.forecast.WeeklyForecast
import org.example.quiversync.features.BaseViewModel

class ForecastViewModel: BaseViewModel() {
    private val _uiState = MutableStateFlow<ForecastState>(ForecastState.Loading)
    val uiState: StateFlow<ForecastState> get() = _uiState


    init {
        fetchForecast()
    }

    private fun fetchForecast() {
        scope.launch {
            val forecast = createMockForecast()
            delay(1500)

            _uiState.emit(
                ForecastState.Loaded(forecast)
            )
        }
    }
}


private fun createMockForecast(): WeeklyForecast {
    val mockWeeklyForecast = WeeklyForecast(
        list = listOf(
            DailyForecast(
                date = "2025-06-21",
                waveHeight = 0.63,
                windSpeed = 3.37,
                windDirection = 248.61,
                swellPeriod = 5.89,
                swellDirection = 294.67
            ),
            DailyForecast(
                date = "2025-06-22",
                waveHeight = 0.77,
                windSpeed = 3.54,
                windDirection = 227.29,
                swellPeriod = 6.33,
                swellDirection = 293.84
            ),
            DailyForecast(
                date = "2025-06-23",
                waveHeight = 1.04,
                windSpeed = 3.06,
                windDirection = 226.87,
                swellPeriod = 7.34,
                swellDirection = 294.53
            ),
            DailyForecast(
                date = "2025-06-24",
                waveHeight = 0.81,
                windSpeed = 2.83,
                windDirection = 229.92,
                swellPeriod = 7.34,
                swellDirection = 298.98
            ),
            DailyForecast(
                date = "2025-06-25",
                waveHeight = 0.58,
                windSpeed = 2.84,
                windDirection = 226.47,
                swellPeriod = 6.33,
                swellDirection = 298.79
            ),
            DailyForecast(
                date = "2025-06-26",
                waveHeight = 0.47,
                windSpeed = 3.06,
                windDirection = 245.2,
                swellPeriod = 6.0,
                swellDirection = 297.6
            )
        )
    )

    return mockWeeklyForecast

}