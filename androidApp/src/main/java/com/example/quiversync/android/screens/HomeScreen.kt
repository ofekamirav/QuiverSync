package com.example.quiversync.android.screens

import androidx.compose.foundation.Image
import com.example.quiversync.android.MyApplicationTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quiversync.android.R

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf9fafb))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("QuiverSync", color = Color(0xFF3366FF), style = MaterialTheme.typography.titleLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Color(0xFF3366FF),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Pipeline, North Shore", color = Color.Gray)
        }

        // Current Conditions Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Current Conditions", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    ConditionItem("Wave Height", "4-6ft", R.drawable.ic_waves)
                    ConditionItem("Wind", "12mph", R.drawable.ic_air)
                    ConditionItem("Tide", "Rising", R.drawable.ic_tide)
                }
            }
        }

        // Board Recommendation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
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

        // Weekly Forecast
        Text("Weekly Forecast", style = MaterialTheme.typography.bodyMedium)
        ForecastItem("Mon", "Jan 15", "3-4ft", "15mph")
        ForecastItem("Tue", "Jan 16", "4-5ft", "12mph")
    }
}

@Composable
fun ConditionItem(label: String, value: String, icon: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        modifier = Modifier.fillMaxWidth()
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
fun PreviewHomeScreen(){
    MyApplicationTheme {
        HomeScreen()
    }

}
