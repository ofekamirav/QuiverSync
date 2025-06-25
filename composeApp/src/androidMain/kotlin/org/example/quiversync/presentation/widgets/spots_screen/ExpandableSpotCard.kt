package org.example.quiversync.presentation.widgets.spots_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import org.example.quiversync.R
import org.example.quiversync.domain.model.FavoriteSpot
import org.example.quiversync.presentation.theme.OceanPalette


@Composable
fun ExpandableSpotCard(spot: FavoriteSpot) {
    var expanded by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White

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
                                    text = spot.recommendedBoardId,
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