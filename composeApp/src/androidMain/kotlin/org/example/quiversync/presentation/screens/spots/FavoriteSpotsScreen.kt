package org.example.quiversync.presentation.screens.spots

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.R
import org.example.quiversync.model.FavoriteSpot
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme


@Composable
fun FavoriteSpotsScreen(spots: List<FavoriteSpot>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        spots.forEach { spot ->
            ExpandableSpotCard(spot)
        }
        Spacer(modifier = Modifier.weight(1f))
        FloatingActionButton(
            onClick = { /* Handle floating action button click */ },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.End).size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Spot",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ExpandableSpotCard(spot: FavoriteSpot) {
    var expanded by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) OceanPalette.DarkFoamWhite else OceanPalette.FoamWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = spot.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = spot.location,
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
                    Divider(color = OceanPalette.BorderGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.hs_shortboard),
                                contentDescription = "Board",
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 12.dp)
                            )
                            Column {
                                Text(
                                    text = spot.recommendedBoard,
                                    fontWeight = FontWeight.Bold,
                                    color = OceanPalette.DeepBlue
                                )
                                Text(
                                    text = "${spot.confidence}% Match",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // 🌊 Wave height with icon
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_waves),
                                contentDescription = "Wave Height",
                                tint = OceanPalette.SkyBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${spot.waveHeight} ft",
                                color = OceanPalette.DeepBlue,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    ConfidenceProgress(spot.confidence)
                }
            }
        }
    }
}

@Composable
fun ConfidenceProgress(percentage: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "ConfidenceAnim"
    )

    LinearProgressIndicator(
        progress = animatedProgress,
        color = OceanPalette.SurfBlue,
        trackColor = OceanPalette.SkyBlue.copy(alpha = 0.3f),
        modifier = Modifier
            .width(100.dp)
            .height(6.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}
@Preview(showBackground = true)
@Composable
fun PreviewFavoriteSpotsScreen() {
    val mockSpots = listOf(
        FavoriteSpot("Pipeline", "North Shore, HI", "Shortboard", 94, "3-4"),
        FavoriteSpot("Trestles", "California", "Fish", 87, "3-4"),
        FavoriteSpot("Snapper Rocks", "Australia", "Funboard", 79, "3-4")
    )
    QuiverSyncTheme {
        FavoriteSpotsScreen(spots = mockSpots)
    }
}
