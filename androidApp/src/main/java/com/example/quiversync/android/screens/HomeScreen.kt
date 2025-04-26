package com.example.quiversync.android.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quiversync.android.MyApplicationTheme
import com.example.quiversync.android.R
import com.example.quiversync.android.navigation.RootNavGraph
import com.ramcosta.composedestinations.annotation.Destination

@RootNavGraph
@Destination<RootNavGraph>
@Composable
fun HomeScreen() {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf9fafb))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "QuiverSync",
            color = Color(0xFF3366FF),
            style = MaterialTheme.typography.titleLarge
        )

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
                tint = Color(0xFF3366FF),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Pipeline, North Shore",
                color = Color.Gray,
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
                waveHeight = "3-4ft",
                wind = "15mph",
                tide = "Rising"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Board Recommendation Card
        Card(
            modifier = Modifier.fillMaxWidth()
                .background( Color(0xFFFFFFFF), RoundedCornerShape(30.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Today's Board Recommendation", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.hs_shortboard),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Shortboard", fontWeight = FontWeight.Bold)
                        Text("5\"10 \" x 19\" x 2.3\"", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Chip(text = "Perfect Match", color = Color(0xFFE5ECFF))
                            Chip(text = "98% Confidence", color = Color(0xFFE6FFF3))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weekly Forecast
        Text("Weekly Forecast", style = MaterialTheme.typography.bodyMedium)
        ForecastItem("Mon", "Jan 15", "3-4ft", "15mph")
        ForecastItem("Tue", "Jan 16", "4-5ft", "12mph")
    }
}

@Composable
fun CurrentConditions(waveHeight: String, wind: String, tide: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
        modifier = Modifier
            .fillMaxWidth()
            .background( Color(0xFFEFF6FF), RoundedCornerShape(30.dp)),

        ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Current Conditions", style = MaterialTheme.typography.bodyMedium)
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
            tint = Color(0xFF3366FF)
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Chip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 12.sp, color = Color.Black)
    }
}

@Composable
fun ForecastItem(day: String, date: String, waveHeight: String, wind: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        modifier = Modifier
            .fillMaxWidth()
            .background( Color(0xFFFFFFFF), RoundedCornerShape(30.dp))
        ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(day, fontWeight = FontWeight.Bold)
                Text(date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_waves),
                contentDescription = null,
                tint = Color(0xFF3366FF)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(waveHeight)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_air),
                contentDescription = null,
                tint = Color(0xFF3366FF)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(wind)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    MyApplicationTheme {
        HomeScreen()
    }
}
