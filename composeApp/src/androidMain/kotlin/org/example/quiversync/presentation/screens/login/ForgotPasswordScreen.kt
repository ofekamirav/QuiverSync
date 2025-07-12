package org.example.quiversync.presentation.screens.login

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.R
import org.example.quiversync.features.login.forgot_password.ForgotPasswordEvent
import org.example.quiversync.features.login.forgot_password.ForgotPasswordFormData
import org.example.quiversync.features.login.forgot_password.ForgotPasswordState
import org.example.quiversync.features.login.forgot_password.ForgotPasswordViewModel
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.components.GradientButton
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = koinViewModel(),
    onLoginClick: () -> Unit = { }
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    (uiState as? ForgotPasswordState.Error)?.let {
        LaunchedEffect(it) {
            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            viewModel.returnToIdle()
        }
    }

    when (val state = uiState) {
        is ForgotPasswordState.Idle -> {
            ForgotPasswordContent(
                modifier = modifier,
                email = state.data.email,
                emailError = state.data.emailError,
                isLoading = false,
                onEvent = viewModel::onEvent
            )
        }
        is ForgotPasswordState.Loading -> {
            ForgotPasswordContent(modifier = modifier, email = "", emailError = null, isLoading = true, onEvent = {})
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(isLoading = true, animationFileName = "quiver_sync_loading_animation.json", animationSize = 240.dp)
            }
        }
        is ForgotPasswordState.Success -> {
            SuccessContent(
                modifier = modifier,
                onLoginClick = onLoginClick,
                message = "A password reset link has been sent. Please check your inbox (and spam folder).",
            )
        }
        is ForgotPasswordState.Error -> {
            ForgotPasswordContent(
                modifier = modifier,
                email = "",
                emailError = null,
                isLoading = false,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
private fun ForgotPasswordContent(
    modifier: Modifier = Modifier,
    email: String,
    emailError: String?,
    isLoading: Boolean,
    onEvent: (ForgotPasswordEvent) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val backgroundBrush = Brush.verticalGradient(
        if (isDark) OceanPalette.LoginGradientDark else OceanPalette.LoginGradientLight
    )
    val windowInfo = LocalWindowInfo.current
    val cardColor = if (isDark) OceanPalette.DarkCard else Color.White

    Box(
        modifier = modifier.fillMaxSize().background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .then(
                    if (windowInfo.widthSize == WindowWidthSize.COMPACT) Modifier.fillMaxWidth(0.9f)
                    else Modifier.widthIn(max = 420.dp)
                )
                .background(cardColor, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Reset Your Password", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Enter your account's email and we will send you a link to get back into your account.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            CustomTextField(
                value = email,
                onValueChange = { onEvent(ForgotPasswordEvent.EmailChanged(it)) },
                label = "Email",
                isError = emailError != null,
                errorMessage = emailError,
                leadingIcon = Icons.Default.MailOutline,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Email,
            )
            Spacer(modifier = Modifier.height(24.dp))
            GradientButton(
                text = "Send Reset Link",
                onClick = { onEvent(ForgotPasswordEvent.SendResetLink) },
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f),
            )
        }
    }
}

@Composable
private fun SuccessContent(
    modifier: Modifier = Modifier,
    message: String,
    onLoginClick: () -> Unit = { }
) {
    val isDark = isSystemInDarkTheme()
    val backgroundBrush = Brush.verticalGradient(
        if (isDark) OceanPalette.LoginGradientDark else OceanPalette.LoginGradientLight
    )
    val windowInfo = LocalWindowInfo.current
    val cardColor = if (isDark) OceanPalette.DarkCard else Color.White

    Box(
        modifier = modifier.fillMaxSize().background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .then(
                    if (windowInfo.widthSize == WindowWidthSize.COMPACT) Modifier.fillMaxWidth(0.9f)
                    else Modifier.widthIn(max = 420.dp)
                )
                .background(cardColor, RoundedCornerShape(16.dp))
                .padding(vertical = 32.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(color = MaterialTheme.colorScheme.secondary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Check Your Inbox", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            GradientButton(
                text = "Back to Login",
                onClick = { onLoginClick() },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordContentPreview() {
    QuiverSyncTheme {
        ForgotPasswordContent(email = "", emailError = null, isLoading = false, onEvent = {})
    }
}