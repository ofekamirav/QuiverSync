package org.example.quiversync.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.R
import org.example.quiversync.domain.model.User
import org.example.quiversync.features.user.UserState
import org.example.quiversync.features.user.UserViewModel
import org.example.quiversync.presentation.components.ErrorContent
import org.example.quiversync.presentation.screens.skeletons.ProfileSkeleton
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.widgets.profile_screen.ProfileHeader
import org.example.quiversync.presentation.widgets.profile_screen.UserDetailsSection
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = UserViewModel(),
    onLogout: () -> Unit,
    onEdit: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsState().value
    when (uiState) {
        is UserState.Loading->{
            ProfileSkeleton(modifier)
        }
        is UserState.Loaded -> {
            ProfileScreenContent(
                modifier = modifier,
                user = uiState.user,
                onLogout = onLogout,
                onEdit = onEdit
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
    user: User,
    onLogout: () -> Unit,
    onEdit: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    when (windowInfo.widthSize) {
        WindowWidthSize.COMPACT -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ProfileHeader(user)
                UserDetailsSection(user)
                Spacer(modifier = Modifier.weight(1f))
                ProfileActionButtons(onLogout = onLogout, onEdit = onEdit)
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
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    ProfileHeader(user)
                    Spacer(modifier = Modifier.weight(1f))
                    ProfileActionButtons(onLogout = onLogout, onEdit = onEdit)
                }
                Column(modifier = Modifier.weight(0.6f)) {
                    UserDetailsSection(user)
                }
            }
        }
    }
}

@Composable
fun ProfileActionButtons(onLogout: () -> Unit, onEdit: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = OceanPalette.SandOrange),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.width(140.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = null,
                tint = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text("Log Out", color = Color.White, fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = onEdit,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.width(140.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = null,
                tint = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text("Edit", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    QuiverSyncTheme() {
        ProfileScreen(
            onLogout = {},
            onEdit = {}
        )
    }
}
