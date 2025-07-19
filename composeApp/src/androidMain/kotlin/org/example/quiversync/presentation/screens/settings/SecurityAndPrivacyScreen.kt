package org.example.quiversync.presentation.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.features.settings.SecurityAndPrivacyEvent
import org.example.quiversync.features.settings.SecurityAndPrivacyFormData
import org.example.quiversync.features.settings.SecurityAndPrivacyState
import org.example.quiversync.features.settings.SecurityAndPrivacyViewModel
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.components.GradientButton
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SecurityAndPrivacyScreen(
    modifier: Modifier = Modifier,
    viewModel: SecurityAndPrivacyViewModel = koinViewModel(),
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is SecurityAndPrivacyState.Editing -> {
            SecurityAndPrivacyContent(
                modifier = modifier,
                form = state.form,
                onEvent = viewModel::onEvent
            )
        }
        is SecurityAndPrivacyState.Loading -> {
            SecurityAndPrivacyContent(
                modifier = modifier,
                form = SecurityAndPrivacyFormData(),
                onEvent = viewModel::onEvent
            )
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(isLoading = true, animationFileName = "quiver_sync_loading_animation.json", animationSize = 240.dp)
            }
        }
        is SecurityAndPrivacyState.Error -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        is SecurityAndPrivacyState.Success -> {
            Toast.makeText(LocalContext.current, "Password changed successfully", Toast.LENGTH_SHORT).show()
            LaunchedEffect(Unit) {
                onSuccess()
            }
        }
    }
}

@Composable
private fun SecurityAndPrivacyContent(
    modifier: Modifier = Modifier,
    form: SecurityAndPrivacyFormData,
    onEvent: (SecurityAndPrivacyEvent) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
            CustomTextField(
                value = form.currentPassword,
                onValueChange = { onEvent(SecurityAndPrivacyEvent.onCurrentPasswordChange(it)) },
                label = "Current Password",
                modifier = Modifier.fillMaxWidth(),
                isError = form.error != null
            )
            CustomTextField(
                value = form.newPassword,
                onValueChange = { onEvent(SecurityAndPrivacyEvent.onNewPasswordChange(it)) },
                label = "New Password",
                modifier = Modifier.fillMaxWidth(),
                isError = form.error != null
            )
            CustomTextField(
                value = form.confirmPassword,
                onValueChange = { onEvent(SecurityAndPrivacyEvent.onConfirmPasswordChange(it)) },
                label = "Confirm New Password",
                modifier = Modifier.fillMaxWidth(),
                isError = form.error != null,
                errorMessage = form.error
            )
            Spacer(modifier = Modifier.weight(1f))
            GradientButton(
                text = "Change Password",
                onClick = { onEvent(SecurityAndPrivacyEvent.OnChangePasswordClicked) },
                enabled = !form.isPasswordLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
    }
}

@Preview(showBackground = true)
@Composable
fun SecurityAndPrivacyScreenPreview() {
    QuiverSyncTheme {
        SecurityAndPrivacyContent(
            form = SecurityAndPrivacyFormData(isPasswordSignIn = true),
            onEvent = {}
        )
    }
}