package com.example.quiversync.android.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quiversync.android.R
import com.example.quiversync.android.navigation.RootNavGraph
import com.ramcosta.composedestinations.annotation.Destination

@RootNavGraph
@Destination<RootNavGraph>
@Composable
fun ProfileScreen(
    user: UserProfile,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf9fafb))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Profile",
            color = Color(0xFF3366FF),
            style = MaterialTheme.typography.titleLarge
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box {
                Spacer(Modifier.height(22.dp))
                Image(
                    painter = painterResource(R.drawable.hs_shortboard),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                IconButton(
                    onClick = { /* change photo */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id= R.drawable.ic_camera),
                        contentDescription = null,
                        tint = Color.Unspecified)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(user.name, style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    tint = Color.LightGray,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(2.dp))
                Text(user.location, color = Color.Gray, fontSize = 14.sp)

            }

            Spacer(Modifier.height(22.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatItem("Reviews", user.reviews.toString())
                VerticalDivider()
                StatItem("Boards", user.boards.toString())
                VerticalDivider()
                StatItem("Spots", user.spots.toString())
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            UserDetailItem(R.drawable.ic_mail,"Email", user.email)
            Divider(color = Color.LightGray, thickness = 0.3.dp)
            UserDetailItem(R.drawable.ic_date,"Date of Birth", user.dateOfBirth)
            Divider(color = Color.LightGray, thickness = 0.3.dp)
            UserDetailItem(R.drawable.ic_height,"Height", "${user.heightCm} cm")
            Divider(color = Color.LightGray, thickness = 0.3.dp)
            UserDetailItem(R.drawable.ic_weight,"Weight", "${user.weightKg} kg")
            Divider(color = Color.LightGray, thickness = 0.3.dp)
            UserDetailItem(R.drawable.ic_quiver,"Surf Level", user.surfLevel)

            Spacer(Modifier.height(16.dp))

        }

        Spacer(Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .padding(16.dp)
                    .width(130.dp)
            ) {
                Icon(painter = painterResource(id= R.drawable.ic_logout), contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Log Out",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                )
            }

            Button(
                onClick = { /* Navigate to edit screen */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF397ff4)),
                modifier = Modifier
                    .padding(16.dp)
                    .width(130.dp)
            ) {
                Icon(painter = painterResource(id= R.drawable.ic_edit), contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Edit",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                )
            }


        }


    }
}

@Composable
fun UserDetailItem(icon: Int, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color.Black
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(text = label, fontWeight = FontWeight.SemiBold)
                Text(text = value, color = Color.Gray)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        user = UserProfile(
            name = "Mike Rodriguez",
            location = "San Diego, CA",
            imageUrl = "https://via.placeholder.com/150",
            boards = 8,
            reviews = 5,
            spots = 12,
            heightCm = 169,
            weightKg = 62,
            surfLevel = "Intermediate",
            email = "MikeRod@gmail.com",
            dateOfBirth = "01/01/2000"
        ),
        onLogout = { /* handle logout */ }
    )
}
