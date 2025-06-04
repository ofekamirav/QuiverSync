package org.example.quiversync.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.unit.sp
import org.example.quiversync.R
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme

@Composable
fun HomeScreen() {
    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
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
                "Pipeline, North Shore",
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
            CurrentConditions("3-4ft", "15mph", "Rising")
        }

        BoardRecommendationCard()

        Text(
            "Weekly Forecast",
            style = MaterialTheme.typography.titleSmall,
            color = OceanPalette.DeepBlue
        )

        ForecastItem("Mon", "Jan 15", "3-4ft", "15mph")
        ForecastItem("Tue", "Jan 16", "4-5ft", "12mph")
    }
}

@Composable
fun CurrentConditions(waveHeight: String, wind: String, tide: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isSystemInDarkTheme()) OceanPalette.DarkFoamWhite else OceanPalette.FoamWhite),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Current Conditions", style = MaterialTheme.typography.titleMedium, color = OceanPalette.DeepBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                ConditionItem("Wave Height", waveHeight, R.drawable.ic_waves)
                ConditionItem("Wind", wind, R.drawable.ic_air)
                ConditionItem("Tide", tide, R.drawable.ic_tide)
            }
        }
    }
}

@Composable
fun ConditionItem(label: String, value: String, icon: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (isSystemInDarkTheme()) OceanPalette.DeepBlue else OceanPalette.SkyBlue
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Bold, color = OceanPalette.DeepBlue)
    }
}

@Composable
fun BoardRecommendationCard() {
    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Today's Board Recommendation", style = MaterialTheme.typography.titleMedium, color = OceanPalette.DeepBlue)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.hs_shortboard),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Shortboard", fontWeight = FontWeight.Bold, color = OceanPalette.DeepBlue)
                    Text("5\"10 \" x 19\" x 2.3\"", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Chip("Perfect Match", OceanPalette.SkyBlue.copy(alpha = 0.1f))
                        Chip("98% Confidence", Color(0xFFDFFFEF))
                    }
                }
            }
        }
    }
}

@Composable
fun Chip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 12.sp, color = OceanPalette.DeepBlue)
    }
}

@Composable
fun ForecastItem(day: String, date: String, waveHeight: String, wind: String) {
    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(day, fontWeight = FontWeight.Bold, color = OceanPalette.DeepBlue)
                Text(date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_waves),
                    contentDescription = null,
                    tint = OceanPalette.SkyBlue
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(waveHeight, color = OceanPalette.DeepBlue)

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_air),
                    contentDescription = null,
                    tint = OceanPalette.SkyBlue
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(wind, color = OceanPalette.DeepBlue)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    QuiverSyncTheme() {
        HomeScreen()
    }
}
