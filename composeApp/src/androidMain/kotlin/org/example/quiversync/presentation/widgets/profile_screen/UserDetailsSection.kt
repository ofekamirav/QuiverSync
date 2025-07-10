package org.example.quiversync.presentation.widgets.profile_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.quiversync.R
import org.example.quiversync.domain.model.User

@Composable
fun UserDetailsSection(user: User){
    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardColor)
            .padding(vertical = 8.dp)
    ) {
        UserDetailItem(R.drawable.ic_mail, "Email", user.email)
        Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
        UserDetailItem(R.drawable.ic_date, "Date of Birth", user.dateOfBirth.toString())
        Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
        UserDetailItem(R.drawable.ic_height, "Height", "${user.heightCm} cm")
        Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
        UserDetailItem(R.drawable.ic_weight, "Weight", "${user.weightKg} kg")
        Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
        UserDetailItem(R.drawable.ic_quiver, "Surf Level", user.surfLevel.toString())
    }
}