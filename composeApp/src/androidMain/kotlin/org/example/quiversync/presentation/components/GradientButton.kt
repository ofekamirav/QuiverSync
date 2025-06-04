package org.example.quiversync.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import org.example.quiversync.presentation.theme.OceanPalette

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()

    val buttonBrush = when {
        !enabled -> Brush.horizontalGradient(
            colors = listOf(
                OceanPalette.SurfBlue.copy(alpha = 0.3f),
                OceanPalette.SkyBlue.copy(alpha = 0.3f)
            )
        )
        isPressed.value -> Brush.horizontalGradient(
            colors = listOf(
                OceanPalette.SurfBlue.copy(alpha = 0.85f),
                OceanPalette.SkyBlue.copy(alpha = 0.85f)
            )
        )
        else -> Brush.horizontalGradient(
            colors = listOf(OceanPalette.SkyBlue,OceanPalette.SurfBlue)
        )
    }

    Box(
        modifier = modifier
            .padding(8.dp)
            .height(48.dp)
            .fillMaxWidth()
            .background(
                brush = buttonBrush,
                shape = shape
            )
            .then(
                if (enabled) Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

