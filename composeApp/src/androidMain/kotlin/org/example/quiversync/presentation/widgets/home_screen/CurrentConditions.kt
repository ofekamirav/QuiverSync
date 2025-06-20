package org.example.quiversync.presentation.widgets.home_screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.quiversync.R
import org.example.quiversync.presentation.theme.OceanPalette

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
