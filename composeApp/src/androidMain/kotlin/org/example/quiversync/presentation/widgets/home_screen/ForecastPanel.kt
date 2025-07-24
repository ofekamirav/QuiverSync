package org.example.quiversync.presentation.widgets.home_screen

import android.icu.text.DecimalFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.forecast.WeeklyForecast
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.utils.extentions.UnitConverter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ForecastPanel(
    forecast: List<DailyForecast>,
    isImperialUnits: Boolean
) {
    val upcomingDays = forecast.drop(1)
    val waveHeightUnit = if (isImperialUnits) "ft" else "m"
    val windSpeedUnit = if (isImperialUnits) "knots" else "m/s"
    val df = DecimalFormat("#.##")

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
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                upcomingDays.forEach { day ->
                    val displayWaveHeight = if (isImperialUnits)
                        UnitConverter.metersToFeet(day.waveHeight) else day.waveHeight
                    val displayWindSpeed = if (isImperialUnits)
                        UnitConverter.msToKnots(day.windSpeed) else day.windSpeed

                    ForecastItem(
                        day = day.date,
                        date = day.date,
                        waveHeight = "${df.format(displayWaveHeight)} $waveHeightUnit",
                        wind = "${df.format(displayWindSpeed)} $windSpeedUnit",
                    )
                }
            }
        }
    }
}
