package org.example.quiversync.presentation.widgets.home_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.example.quiversync.R
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.presentation.theme.OceanPalette


@Composable
fun BoardRecommendationCard(surfboard: Surfboard, score: String) {
    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
    val scoreDescription = when {
        (score.toIntOrNull() ?: 0) >= 80 -> "Perfect Match"
        (score.toIntOrNull() ?: 0) >= 50 -> "Good Match"
        else -> "Consider Renting"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Today's Board Recommendation", style = MaterialTheme.typography.titleMedium, color = OceanPalette.DeepBlue)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = surfboard.imageRes,
                    placeholder = painterResource(id = R.drawable.logo_placeholder),
                    contentDescription = "Surfboard Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(60.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(surfboard.model, fontWeight = FontWeight.Bold, color = OceanPalette.DeepBlue)
                    Text("${surfboard.height} x ${surfboard.width} x ${surfboard.volume}L", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Chip(scoreDescription, OceanPalette.SkyBlue.copy(alpha = 0.1f))
                        Chip("${score}% Confidence", Color(0xFFDFFFEF))
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