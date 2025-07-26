package org.example.quiversync.presentation.widgets.spots_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import org.example.quiversync.R
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.FinsSetup
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.SurfboardType
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.domain.model.forecast.WeeklyForecast
import org.example.quiversync.domain.model.prediction.DailyPrediction
import org.example.quiversync.domain.model.prediction.GeminiPrediction
import org.example.quiversync.features.spots.fav_spot_main_page.FavSpotsData
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.utils.extentions.UnitConverter
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyForecastPopup(
    data: FavSpotsData,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    isImperial: Boolean
) {
    val hasBoards = data.boards.isNotEmpty()

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            val backgroundColor = if (isSystemInDarkTheme()) OceanPalette.DarkSurface.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.9f)
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                shape = RoundedCornerShape(24.dp),
                color = backgroundColor
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
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onClose) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Divider(color = OceanPalette.BorderGray, thickness = 1.dp)

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (hasBoards) {
                            items(data.weeklyForecastForSpecificSpot.size) { item ->
                                val surfboardId =
                                    data.weeklyPredictionsForSpecificSpot[item].surfboardID
                                WeeklyForecastItem(
                                    data = WeeklyForecastData(
                                        date = data.weeklyForecastForSpecificSpot[item].date,
                                        model = data.boards.find { it.id == surfboardId }?.model
                                            ?: "Unknown Model",
                                        company = data.boards.find { it.id == surfboardId }?.company
                                            ?: "Unknown Company",
                                        imageUrl = data.boards.find { it.id == surfboardId }?.imageRes
                                            ?: R.drawable.hs_shortboard.toString(),
                                        matchPercent = data.weeklyPredictionsForSpecificSpot[item].score,
                                        waveHeight = data.weeklyForecastForSpecificSpot[item].waveHeight,
                                        windSpeed = data.weeklyForecastForSpecificSpot[item].windSpeed,
                                        swellPeriod = data.weeklyForecastForSpecificSpot[item].swellPeriod
                                    ),
                                    isImperial = isImperial
                                )
                                Divider(color = OceanPalette.BorderGray, thickness = 0.5.dp)
                            }
                        } else{
                            items(data.weeklyForecastForSpecificSpot.size) { index ->
                                ForecastOnlyItem(
                                    forecast = data.weeklyForecastForSpecificSpot[index],
                                    isImperial = isImperial
                                )
                                Divider(color = OceanPalette.BorderGray, thickness = 0.5.dp)
                            }
                        }
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
    var waveHeight: Double,
    var windSpeed: Double,
    val swellPeriod: Double
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyForecastItem(
    data: WeeklyForecastData,
    isImperial: Boolean,
    modifier: Modifier = Modifier,
) {
    val df = DecimalFormat("#.##")

    val waveValue =
        if (isImperial) df.format(UnitConverter.metersToFeet(data.waveHeight)) else df.format(data.waveHeight)
    val windValue =
        if (isImperial) df.format(UnitConverter.msToKnots(data.windSpeed)) else df.format(data.windSpeed)
    val waveUnit = if (isImperial) "ft" else "m"
    val windUnit = if (isImperial) "knots" else "m/s"
    val dayName = remember(data.date) {
        runCatching {
            LocalDate.parse(data.date).dayOfWeek
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        }.getOrNull() ?: data.date
    }
    val swellPeriod = df.format(data.swellPeriod)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = data.imageUrl,
                    placeholder = painterResource(id = R.drawable.logo_placeholder),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = data.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = data.model,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(OceanPalette.SkyBlue.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${data.matchPercent}% match",
                        style = MaterialTheme.typography.bodySmall,
                        color = OceanPalette.SurfBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_waves),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$waveValue $waveUnit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_air),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$windValue $windUnit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_tide),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$swellPeriod s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun WeeklyForecastPopupPreview() {
    val sampleData = FavSpotsData(
        weeklyForecastForSpecificSpot = listOf(
            DailyForecast(
                date = "2023-10-01",
                waveHeight = 1.5,
                windSpeed = 5.0
            )
        ),
        weeklyPredictionsForSpecificSpot = listOf(
            GeminiPrediction(
                surfboardID = "1",
                score = 85
            ),
            GeminiPrediction(
                surfboardID = "2",
                score = 90
            )
        ),
        boards = listOf(
            Surfboard(
                id = "1",
                model = "Shortboard",
                company = "SurfCo",
                imageRes = "R.drawable.hs_shortboard.toString()",
                height = "5.10",
                width = "18.5",
                volume = "28.0",
                finSetup = FinsSetup.THRUSTER,
                type = SurfboardType.FUNBOARD,
                ownerId = "user123",
                addedDate = "2023-09-15",
            ),
            Surfboard(
                id = "2",
                model = "Fish",
                company = "WaveRider",
                imageRes = "R.drawable.hs_fish.toString()",
                height = "5.6",
                width = "20.0",
                volume = "32.0",
                finSetup = FinsSetup.TWIN,
                type = SurfboardType.FISHBOARD,
                ownerId = "user123",
                addedDate = "2023-09-20"
            )
        ),
        spots = listOf(
            FavoriteSpot(
                spotID = "1",
                name = "Mavericks, California",
                spotLatitude = 37.4935,
                spotLongitude = -122.5049,
                userID = "user123"
            )
        ),
        allSpotsDailyPredictions = listOf(
            GeminiPrediction(
                surfboardID = "1",
                score = 80
            ),
            GeminiPrediction(
                surfboardID = "2",
                score = 90
            )
        ),
        currentForecastsForAllSpots = listOf(
            DailyForecast(
                date = "2023-10-01",
                waveHeight = 1.5,
                windSpeed = 5.0,
                windDirection = 0.1,
                swellDirection = 0.2,
                swellPeriod = 12.0
            )
        ),
        weeklyUiPredictions = listOf(
            DailyPrediction(
                prediction = GeminiPrediction(
                    surfboardID = "1",
                    score = 85
                ),
                surfboard = Surfboard(
                    id = "1",
                    model = "Shortboard",
                    company = "SurfCo",
                    imageRes = "R.drawable.hs_shortboard.toString()",
                    height = "5.10",
                    width = "18.5",
                    volume = "28.0",
                    finSetup = FinsSetup.THRUSTER,
                    type = SurfboardType.FUNBOARD,
                    ownerId = "user123",
                    addedDate = "2023-09-15"
                ),
                dailyForecast = DailyForecast(
                    date = "2023-10-01",
                    waveHeight = 1.5,
                    windSpeed = 5.0,
                    windDirection = 0.1,
                    swellDirection = 0.2,
                    swellPeriod = 12.0
                )
            )
        )
    )
    QuiverSyncTheme {
        WeeklyForecastPopup(
            data = sampleData,
            onClose = {},
            isImperial = false
        )
    }
}

