package org.example.quiversync.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.example.quiversync.presentation.theme.OceanPalette

@Composable
fun SocialLoginButton(
    text: String,
    logo: Int,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = OceanPalette.FoamWhite,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, OceanPalette.BorderGray),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .height(48.dp)
            .width(140.dp)
    ) {
        Icon(
            painter = painterResource(id = logo),
            contentDescription = "$text Logo",
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black
        )
    }
}

