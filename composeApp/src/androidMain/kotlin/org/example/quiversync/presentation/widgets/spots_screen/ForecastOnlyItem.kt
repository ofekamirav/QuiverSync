package org.example.quiversync.presentation.widgets.spots_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.utils.extentions.UnitConverter
import java.text.DecimalFormat
import org.example.quiversync.R

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ForecastOnlyItem(
    forecast: DailyForecast,
    isImperial: Boolean
) {
    val df = DecimalFormat("#.#")
    val waveValue = if (isImperial) df.format(UnitConverter.metersToFeet(forecast.waveHeight)) else df.format(forecast.waveHeight)
    val windValue = if (isImperial) df.format(UnitConverter.msToKnots(forecast.windSpeed)) else df.format(forecast.windSpeed)
    val waveUnit = if (isImperial) "ft" else "m"
    val windUnit = if (isImperial) "knots" else "m/s"
    val swell = df.format(forecast.swellPeriod)
    val dayName = UnitConverter.getDayOfWeekName(forecast.date)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = dayName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = forecast.date,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            InfoItem(R.drawable.ic_waves, "$waveValue $waveUnit")
            InfoItem(R.drawable.ic_air, "$windValue $windUnit")
            InfoItem(R.drawable.ic_tide, "$swell s")
        }
    }
}
