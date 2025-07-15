package org.example.quiversync.presentation.widgets.home_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import org.example.quiversync.utils.extentions.UnitConverter

@RequiresApi(Build.VERSION_CODES.O)
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
                Text(UnitConverter.getDayOfWeekName(day), fontWeight = FontWeight.Bold, color = OceanPalette.DeepBlue)
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