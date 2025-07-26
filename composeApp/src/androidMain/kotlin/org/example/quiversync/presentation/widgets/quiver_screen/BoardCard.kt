package org.example.quiversync.presentation.widgets.quiver_screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.example.quiversync.R
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.presentation.theme.OceanPalette


@Composable
fun BoardCard(
    board: Surfboard,
    onClick: () -> Unit,
    onPublishToggle: (Surfboard, Boolean) -> Unit
) {
    val isPublished = board.isRentalPublished ?: false
    val isDark = isSystemInDarkTheme()
    val baseBackgroundColor = if (isDark) OceanPalette.DarkSurface else Color.White
    val animatedBackgroundColor by animateColorAsState(
        targetValue = baseBackgroundColor,
        animationSpec = tween(300)
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isPublished) OceanPalette.SandOrange else Color.Transparent,
        animationSpec = tween(300)
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (isPublished) 8.dp else 4.dp,
        animationSpec = tween(300)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
        colors = CardDefaults.cardColors(containerColor = animatedBackgroundColor),
        border = BorderStroke(2.dp, animatedBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = board.imageRes,
                placeholder = painterResource(id = R.drawable.logo_placeholder),
                contentDescription = "Surfboard Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Fit
            )
            Text(
                board.model,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = OceanPalette.DeepBlue
            )
            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "For Rent",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Switch(
                    checked = board.isRentalPublished ?: false,
                    onCheckedChange = { checked ->
                        onPublishToggle(board, checked)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = OceanPalette.SandOrange,
                        checkedTrackColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f),
                        uncheckedThumbColor = OceanPalette.SandOrange.copy(alpha = 0.4f),
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.4f),
                        uncheckedBorderColor = Color.Transparent,
                    )
                )
            }
        }
    }
}