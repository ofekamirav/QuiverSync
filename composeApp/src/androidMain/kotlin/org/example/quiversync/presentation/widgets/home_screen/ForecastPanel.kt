package org.example.quiversync.presentation.widgets.home_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.example.quiversync.presentation.theme.OceanPalette

@Composable
fun ForecastPanel() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Weekly Forecast",
            style = MaterialTheme.typography.titleSmall,
            color = OceanPalette.DeepBlue
        )
        // Should be Lazy Column and get the data from the API
        ForecastItem("Mon", "Jan 15", "3-4ft", "15mph")
        ForecastItem("Tue", "Jan 16", "4-5ft", "12mph")
    }
}