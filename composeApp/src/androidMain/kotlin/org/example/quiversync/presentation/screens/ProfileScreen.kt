package org.example.quiversync.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.quiversync.R
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme

@Composable
fun ProfileScreen(
    user: UserProfile,
    onLogout: () -> Unit,
    onEdit: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Avatar & Name
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box {
                Image(
                    painter = painterResource(R.drawable.hs_shortboard),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                IconButton(
                    onClick = { /* change photo */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = OceanPalette.DeepBlue
            )

        }

        // Stats Section
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 16.dp)
        ) {
            StatItem("Rentals", user.reviews.toString())
            VerticalDivider()
            StatItem("Boards", user.boards.toString())
            VerticalDivider()
            StatItem("Spots", user.spots.toString())
        }

        // Details Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardColor)
                .padding(vertical = 12.dp)
        ) {
            UserDetailItem(R.drawable.ic_mail, "Email", user.email)
            Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
            UserDetailItem(R.drawable.ic_date, "Date of Birth", user.dateOfBirth)
            Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
            UserDetailItem(R.drawable.ic_height, "Height", "${user.heightCm} cm")
            Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
            UserDetailItem(R.drawable.ic_weight, "Weight", "${user.weightKg} kg")
            Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
            UserDetailItem(R.drawable.ic_quiver, "Surf Level", user.surfLevel)
        }

        Spacer(modifier = Modifier.weight(1f))

        ProfileActionButtons(onLogout = onLogout, onEdit = onEdit)
    }
}

@Composable
fun UserDetailItem(icon: Int, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 13.sp, color = Color.Gray)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        Text(label, color = Color.Gray, fontSize = 13.sp)
    }
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .height(36.dp)
            .width(1.dp)
            .background(Color.LightGray)
    )
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

data class UserProfile(
    val name: String,
    val location: String,
    val imageUrl: String,
    val reviews: Int,
    val boards: Int,
    val spots: Int,
    val dateOfBirth: String,
    val heightCm: Int,
    val weightKg: Int,
    val surfLevel: String,
    val email: String
)

@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreview() {
    QuiverSyncTheme(darkTheme = true) {
        ProfileScreen(
            user = UserProfile(
                name = "Mike Rodriguez",
                location = "San Diego, CA",
                imageUrl = "",
                boards = 8,
                reviews = 5,
                spots = 12,
                heightCm = 169,
                weightKg = 62,
                surfLevel = "Intermediate",
                email = "MikeRod@gmail.com",
                dateOfBirth = "01/01/2000"
            ),
            onLogout = { },
            onEdit = { }
        )
    }
}
