package org.example.quiversync.presentation.widgets.home_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.forecast.WeeklyForecast
import org.example.quiversync.presentation.theme.OceanPalette

@Composable
fun ForecastPanel(
    forecast: List<DailyForecast>
) {
    val upcomingDays = forecast.drop(1)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Weekly Forecast",
            style = MaterialTheme.typography.titleSmall,
            color = OceanPalette.DeepBlue
        )
        if (upcomingDays.isEmpty()) {
            Text(
                "No more forecast data available.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(upcomingDays) { day ->
                    ForecastItem(
                        day = day.date.take(3),
                        date = day.date,
                        waveHeight = "${day.waveHeight} m",
                        wind = "${day.windSpeed} m/s"
                    )
                }
            }
        }
    }
}