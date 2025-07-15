package org.example.quiversync.presentation.widgets.home_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.presentation.theme.OceanPalette

@Composable
fun MainConditions(
    forecast: DailyForecast,
    prediction: GeminiPrediction?,
    surfboard: Surfboard?,
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandToggle() }
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = OceanPalette.SandOrange,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Your Location",
                color = OceanPalette.DeepBlue,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }

        AnimatedVisibility(visible = expanded) {
            CurrentConditions(
                waveHeight = "${forecast.waveHeight} m",
                wind = "${forecast.windSpeed} m/s",
                tide = "${forecast.swellPeriod} s period"
            )
        }

        if(surfboard?.id == "default"){
            val isDark = isSystemInDarkTheme()
            val cardColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Your quiver is empty",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isDark) Color.LightGray else Color.DarkGray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please add a surfboard to your quiver to get personalized recommendations.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) Color.Gray else Color.DarkGray
                    )

                }
            }
        }
        surfboard?.let { BoardRecommendationCard(surfboard = it, score = prediction?.score.toString()) }
    }
}