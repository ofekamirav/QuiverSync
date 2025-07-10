package org.example.quiversync.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.features.user.UserState
import org.example.quiversync.features.user.UserViewModel
import org.example.quiversync.presentation.components.ErrorContent
import org.example.quiversync.presentation.screens.skeletons.ProfileSkeleton
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.widgets.profile_screen.ProfileHeader
import org.example.quiversync.presentation.widgets.profile_screen.UserDetailsSection
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    when (uiState) {
        is UserState.Loading->{
            ProfileSkeleton(modifier)
        }
        is UserState.Loaded -> {
            ProfileScreenContent(
                modifier = modifier,
                userState = uiState,
            )
        }
        is UserState.Error -> {
            // Handle error state
            ErrorContent(message = uiState.message)
        }

    }

}

@Composable
fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    userState: UserState,
) {
    val windowInfo = LocalWindowInfo.current
    val user = (userState as UserState.Loaded).user

    when (windowInfo.widthSize) {
        WindowWidthSize.COMPACT -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileHeader(userState)
                UserDetailsSection(user)
            }
        }
        else->{
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(0.4f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileHeader(userState)
                }
                Column(modifier = Modifier.weight(0.6f)) {
                    UserDetailsSection(user)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    QuiverSyncTheme() {
        ProfileScreen()
    }
}
