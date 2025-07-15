package org.example.quiversync.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.R
import org.example.quiversync.features.settings.SettingsViewModel
import org.example.quiversync.presentation.components.CustomDialog
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.widgets.settings_screen.SettingItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onEditProfile: () -> Unit,
    onSecuritySettings: () -> Unit,
    onNotificationsSettings: () -> Unit,
    onHelpSupport: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val showSecurityAndPrivacyState by viewModel.showSecurityAndPrivacy.collectAsState()
    val isImperialUnits by viewModel.isImperialUnits.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        CustomDialog(
            title = "Log Out",
            message = "Are you sure you want to log out?",
            onConfirm = {
                showDialog = false
                viewModel.logout { onLogout() }
            },
            onDismiss = { showDialog = false }
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        SettingItem(
            iconRes = Icons.Filled.Edit,
            label = "Edit Profile Details",
            onClick = onEditProfile
        )
        if (showSecurityAndPrivacyState) {
            SettingItem(
                iconRes = Icons.Filled.Security,
                label = "Security & Privacy",
                onClick = onSecuritySettings
            )
        }
        SettingItem(
            iconRes = Icons.Filled.Notifications,
            label = "Notifications",
            onClick = onNotificationsSettings
        )
        SettingItem(
            iconRes = Icons.Filled.Straighten,
            onClick = { viewModel.toggleUnits() },
            label = if (isImperialUnits) "Imperial units" else "Metric units"
        )

        SettingItem(
            iconRes = Icons.Filled.HelpOutline,
            label = "Help & Support",
            onClick = onHelpSupport
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick ={
                showDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = "Log Out",
                tint = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text("Log Out", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            LoadingAnimation(isLoading = true, animationFileName = "quiver_sync_loading_animation.json", animationSize = 240.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    QuiverSyncTheme {
        SettingsScreen(
            onEditProfile = {},
            onSecuritySettings = {},
            onNotificationsSettings = {},
            onHelpSupport = {},
            onLogout = {},
        )
    }
}




