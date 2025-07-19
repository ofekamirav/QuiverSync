package org.example.quiversync.presentation.screens.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.blur
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.example.quiversync.features.login.LoginData
import org.example.quiversync.features.login.LoginEvent
import org.example.quiversync.features.login.LoginState
import org.example.quiversync.features.login.LoginViewModel
import org.example.quiversync.features.spots.add_fav_spot.AddFavSpotState
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.components.GradientButton
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.presentation.components.SocialLoginButton
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.androidx.compose.koinViewModel


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    onRegisterClick: () -> Unit = {},
    onSignInSuccess: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onNavigateToOnboarding: () -> Unit = {}
    ) {
    val uiState by viewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val contentModifier = if (uiState is LoginState.Loading) {
        Modifier.blur(radius = 8.dp)
    } else {
        Modifier
    }

    when (val currentState = uiState) {
        is LoginState.Loading -> {
            LoginScreenContent(currentState = LoginState.Idle(LoginData()), isLoading = true, onRegisterClick = {}, onEvent = viewModel::onEvent,
                onForgotPasswordClick = onForgotPasswordClick, onGoogleSignInResult = viewModel::onGoogleSignInResult, modifier = contentModifier)
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(isLoading = true, animationFileName = "quiver_sync_loading_animation.json", animationSize = 240.dp)
            }
        }
        is LoginState.Error -> {
            LaunchedEffect(currentState) {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
        }
        is LoginState.Loaded -> {
            LaunchedEffect(Unit) {
                onSignInSuccess()
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
        }
        is LoginState.Idle -> {
            LoginScreenContent(
                onRegisterClick = onRegisterClick,
                onEvent = viewModel::onEvent,
                currentState = currentState,
                onForgotPasswordClick = onForgotPasswordClick,
                onGoogleSignInResult = viewModel::onGoogleSignInResult
            )
        }
        is LoginState.NavigateToOnboarding -> {
            LaunchedEffect(Unit) { onNavigateToOnboarding() }
        }

    }
}


@Composable
fun LoginScreenContent(
    currentState: LoginState.Idle,
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onEvent: (LoginEvent) -> Unit = {},
    isLoading: Boolean = false,
    onGoogleSignInResult: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
     val isDark = isSystemInDarkTheme()
     val context = LocalContext.current

    val backgroundBrush = Brush.verticalGradient(
        if (isDark) OceanPalette.LoginGradientDark else OceanPalette.LoginGradientLight
    )
    val cardColor = if (isDark) OceanPalette.DarkCard else Color.White
    val textColor = MaterialTheme.colorScheme.onSurface
    val windowInfo = LocalWindowInfo.current

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // חשוב! לקחת מ-google-services.json
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            onGoogleSignInResult(account?.idToken)
        } catch (e: ApiException) {
            onGoogleSignInResult(null)
        }
    }

    Box(
        modifier = modifier
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
                Modifier.widthIn(max = 400.dp)
                .background(cardColor, RoundedCornerShape(16.dp))
                .padding(20.dp),
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
                value = currentState.data.email,
                onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
                label = "Email",
                isError = currentState.data.emailError != null,
                errorMessage = currentState.data.emailError,
                leadingIcon = Icons.Default.MailOutline,
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = currentState.data.password,
                onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                isError = currentState.data.passwordError != null,
                errorMessage = currentState.data.passwordError,
                isPassword = true,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
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
                    modifier = Modifier.clickable { onForgotPasswordClick() },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            GradientButton(
                text = "Sign In",
                onClick = { onEvent(LoginEvent.SignInClicked) },
                enabled = !isLoading,
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
                SocialLoginButton(windowInfo = windowInfo,text = "Google", logo = R.drawable.google_logo,
                    onClick = { launcher.launch(googleSignInClient.signInIntent) }
                )
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
@Preview(showBackground = true, widthDp = 900)
@Composable
fun LoginScreenPreviewTablet() {
    QuiverSyncTheme() {
        LoginScreen()
    }
}