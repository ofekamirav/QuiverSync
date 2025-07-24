package org.example.quiversync.presentation.widgets.spots_screen

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.example.quiversync.R
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.domain.model.FinsSetup
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.SurfboardType
import org.example.quiversync.domain.model.forecast.DailyForecast
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.utils.extentions.UnitConverter
import java.text.DecimalFormat


@Composable
fun ExpandableSpotCard(
    spot: FavoriteSpot,
    score: String,
    surfboard: Surfboard?,
    forecast: DailyForecast,
    onWeeklyForecastClick: () -> Unit,
    isImperial: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
    val df = DecimalFormat("#.##")
    val waveHeightDisplay = if(isImperial) {
        val feet = UnitConverter.metersToFeet(forecast.waveHeight)
        df.format(feet)
    } else {
        df.format(forecast.waveHeight)
    }
    val waveHeightUnit = if (isImperial) "ft" else "m"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val spotName = spot.name.split(",").first()
        val spotLocationName = spot.name.split(",").getOrNull(1)?.trim() ?: "Unknown Location"
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = spotName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = spotLocationName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (surfboard!=null){
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = surfboard.imageRes,
                                    placeholder = painterResource(id = R.drawable.logo_placeholder),
                                    contentDescription = "Board",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(end = 8.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = surfboard.model,
                                        fontWeight = FontWeight.Bold,
                                        color = OceanPalette.DeepBlue
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    MatchBadge(score.toInt())
                                }
                            }
                        } else{
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.hs_shortboard),
                                    contentDescription = "No Board",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))

                                )
                                Text(
                                    text = "No Recommended Board is available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OceanPalette.DeepBlue
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_waves),
                                contentDescription = "Wave Height",
                                tint = OceanPalette.SkyBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val df = DecimalFormat("#.##")
                            Text(
                                text = "${waveHeightDisplay} ${waveHeightUnit}",
                                color = OceanPalette.DeepBlue,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onWeeklyForecastClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Weekly Forecast",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}


@Preview(name = "Expanded Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ExpandableSpotCardExpandedPreview() {
    var expanded by remember { mutableStateOf(true) } // Keep expanded true for this preview

    val dummySpot = FavoriteSpot(
        spotID = "1",
        name = "Mavericks, California",
        spotLatitude = 37.4935,
        spotLongitude = -122.5049,
        userID = "user123",
    )
    val dummySurfboard = Surfboard(
        id = "2",
        model = "Gun",
        volume = "60L",
        imageRes = "https://example.com/gun_surfboard.png",
        ownerId = "user123",
        company = "Channel Islands",
        type = SurfboardType.FUNBOARD,
        height = "9.0",
        width = "22.0",
        finSetup = FinsSetup.THRUSTER,
        addedDate = "2023-10-01",
    )
    val dummyForecast = DailyForecast(
        date = "2023-10-28",
        waveHeight = 15.2,
        windSpeed = 12.0,
        windDirection = 0.1,
        swellDirection = 0.2,
        swellPeriod = 12.0,
    )

    QuiverSyncTheme(darkTheme = true) {
        ExpandableSpotCard(
            spot = dummySpot,
            score = "85",
            surfboard = dummySurfboard,
            forecast = dummyForecast,
            onWeeklyForecastClick = {}
        )
    }
}