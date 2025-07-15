package org.example.quiversync.presentation.widgets.spots_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import org.example.quiversync.R
import org.example.quiversync.presentation.theme.OceanPalette
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyForecastPopup(
    forecasts: List<WeeklyForecastData>,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weekly Forecast",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Divider(color = OceanPalette.BorderGray, thickness = 1.dp)

                // ======= List of Forecasts =======
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(forecasts.size) { item ->
                        WeeklyForecastItem(data = forecasts[item])
                        Divider(color = OceanPalette.BorderGray, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

data class WeeklyForecastData(
    val date: String,
    val model: String,
    val company: String,
    val imageUrl: String,
    val matchPercent: Int,
    val waveHeight: Double,
    val windSpeed: Double
)

@Composable
fun WeeklyForecastItem(
    data: WeeklyForecastData,
    modifier: Modifier = Modifier
) {
    val df = remember { DecimalFormat("#.##") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = data.imageUrl,
            placeholder = painterResource(id = R.drawable.hs_shortboard),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = data.model,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = OceanPalette.DeepBlue
            )
            Text(
                text = data.date,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(OceanPalette.SkyBlue.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${data.matchPercent}% match",
                style = MaterialTheme.typography.bodySmall,
                color = OceanPalette.SurfBlue
            )
        }

        Spacer(Modifier.width(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_waves),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = OceanPalette.SkyBlue
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${df.format(data.waveHeight)} m",
                style = MaterialTheme.typography.bodyMedium,
                color = OceanPalette.DeepBlue
            )
        }
        Spacer(Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_air),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = OceanPalette.DeepBlue
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${df.format(data.windSpeed)} m/s",
                style = MaterialTheme.typography.bodyMedium,
                color = OceanPalette.DeepBlue
            )
        }
    }
}
