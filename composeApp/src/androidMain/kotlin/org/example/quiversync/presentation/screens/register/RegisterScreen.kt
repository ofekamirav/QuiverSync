package org.example.quiversync.presentation.screens.register


import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.components.GradientButton
import org.example.quiversync.R
import org.example.quiversync.features.register.RegisterEvent
import org.example.quiversync.features.register.RegisterState
import org.example.quiversync.features.register.RegisterViewModel
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = koinViewModel(),
    onSignUpSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val state by viewModel.registerState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (val currentState = state) {
        is RegisterState.Idle -> {
            RegisterScreenContent(
                state = currentState,
                onEvent = viewModel::onEvent,
                onLoginClick = onLoginClick
            )
        }
        is RegisterState.Loading -> {
            RegisterScreenContent(state = RegisterState.Idle(), onEvent = {}, onLoginClick = {}, isLoading = true)
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(isLoading = true, animationFileName = "quiver_sync_loading_animation.json", animationSize = 240.dp)
            }
        }
        is RegisterState.Success -> {
            LaunchedEffect(Unit) {
                onSignUpSuccess()
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
        }
        is RegisterState.Error -> {
            LaunchedEffect(currentState) {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
        }
    }
}



@Composable
fun RegisterScreenContent(
    state: RegisterState.Idle,
    onEvent: (RegisterEvent) -> Unit,
    onLoginClick: () -> Unit,
    isLoading: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    val backgroundBrush = Brush.verticalGradient(
        if (isDark) OceanPalette.LoginGradientDark else OceanPalette.LoginGradientLight
    )
    val cardColor = if (isDark) OceanPalette.DarkCard else Color.White
    val textColor = MaterialTheme.colorScheme.onSurface
    val logoTint = if (isDark) OceanPalette.SkyBlue else OceanPalette.DeepBlue
    val windowInfo = LocalWindowInfo.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = if (windowInfo.widthSize == WindowWidthSize.COMPACT)
                Modifier.fillMaxWidth(0.9f)
                    .background(cardColor, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            else
                Modifier.widthIn(max = 500.dp)
                .background(cardColor, RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                tint = logoTint,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "QuiverSync",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = logoTint,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Create Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = state.name,
                onValueChange = { onEvent(RegisterEvent.NameChanged(it)) },
                label = "Name",
                leadingIcon = Icons.Default.Person,
                isError = state.nameError != null,
                errorMessage = state.nameError,
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = state.email,
                onValueChange = { onEvent(RegisterEvent.EmailChanged(it)) },
                label = "Email",
                leadingIcon = Icons.Default.MailOutline,
                isError = state.emailError != null,
                errorMessage = state.emailError,
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = state.password,
                onValueChange = { onEvent(RegisterEvent.PasswordChanged(it)) },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                isError = state.passwordError != null,
                errorMessage = state.passwordError,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            )


            Spacer(modifier = Modifier.height(16.dp))

            GradientButton(
                text = "Sign Up",
                onClick = { if (!isLoading) onEvent(RegisterEvent.SignUpClicked) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Already have an account? Sign in",
                modifier = Modifier.clickable { onLoginClick() },
                color = logoTint,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

