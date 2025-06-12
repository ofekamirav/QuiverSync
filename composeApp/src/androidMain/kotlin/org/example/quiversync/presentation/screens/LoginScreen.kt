package org.example.quiversync.presentation.screens

import org.example.quiversync.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.components.GradientButton
import org.example.quiversync.presentation.components.SocialLoginButton
import org.example.quiversync.presentation.theme.OceanPalette

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit = {},
    onSignInClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isDark = isSystemInDarkTheme()

    val backgroundBrush = Brush.verticalGradient(
        if (isDark) OceanPalette.LoginGradientDark else OceanPalette.LoginGradientLight
    )
    val cardColor = if (isDark) OceanPalette.DarkCard else Color.White
    val textColor = MaterialTheme.colorScheme.onSurface



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(cardColor, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                tint = if (isDark) OceanPalette.SkyBlue else OceanPalette.DeepBlue,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "QuiverSync",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) OceanPalette.SkyBlue else OceanPalette.DeepBlue,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Welcome Back",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Default.MailOutline,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Forgot Password?",
                    color = textColor,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            GradientButton(
                text = "Sign In",
                onClick = { onSignInClick() },
//                modifier = Modifier.clickable { onSignInClick() },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f), color = textColor)
                Text(
                    "  Or continue with  ",
                    fontSize = 12.sp,
                    color = textColor
                )
                Divider(modifier = Modifier.weight(1f), color = textColor)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialLoginButton(text = "Google", logo = R.drawable.google_logo)
                SocialLoginButton(text = "Apple", logo = R.drawable.apple_logo)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Don't have an account? Sign up",
                modifier = Modifier.clickable { onRegisterClick() },
                color = if (isDark) OceanPalette.SkyBlue else OceanPalette.DeepBlue,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(
    showBackground = true,
    //name = "Dark Mode",
    //uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun LoginScreenDarkPreview() {
    QuiverSyncTheme() {
        LoginScreen()
    }
}
//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    QuiverSyncTheme {
//        LoginScreen()
//    }
//}