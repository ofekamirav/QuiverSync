package org.example.quiversync.presentation.widgets.spots_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.quiversync.domain.model.forecast.DailyForecast

@Composable
fun WeeklyForecastRow(item: DailyForecast) {
//    Column(Modifier.padding(vertical = 8.dp)) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Icon(
//                painterResource(id = R.drawable.ic_surfboard),
//                contentDescription = null,
//                Modifier.size(32.dp)
//            )
//            Spacer(Modifier.width(12.dp))
//            Column {
//                Text(item.boardType, fontWeight = FontWeight.Bold)
//                Text(item.boardName)
//                Text(item.date)
//            }
//        }
//        Spacer(Modifier.height(4.dp))
//        Row(horizontalArrangement = Arrangement.SpaceBetween) {
//            Text(item.match)
//            Text("${item.waveHeight} ~ ${item.windSpeed}")
//        }
//    }
}