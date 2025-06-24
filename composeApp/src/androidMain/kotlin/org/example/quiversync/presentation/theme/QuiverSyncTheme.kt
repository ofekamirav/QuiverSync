package org.example.quiversync.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

@Composable
fun QuiverSyncTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = OceanPalette.SurfBlue,
            onPrimary = Color.Black,
            secondary = OceanPalette.SandOrange,
            background = OceanPalette.DarkBackground,
            surface = OceanPalette.DarkSurface,
            onSurface = OceanPalette.DarkText,
            onSecondary = OceanPalette.DarkSky,
            error = OceanPalette.error
        )
    } else {
        lightColorScheme(
            primary = OceanPalette.DeepBlue,
            onPrimary = Color.White,
            secondary = OceanPalette.SkyBlue,
            background = OceanPalette.background,
            surface = OceanPalette.FoamWhite,
            onSurface = OceanPalette.TextDark,
            onSecondary = OceanPalette.SurfBlue,
            error = OceanPalette.error
        )
    }

    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    )

    val shapes = Shapes(
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(24.dp)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

