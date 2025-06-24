package org.example.quiversync.presentation.screens


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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.components.GradientButton
import org.example.quiversync.R
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.components.DropdownTextField
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize

@Composable
fun RegisterScreen(
    onSignUpClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Create Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomTextField(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                leadingIcon = Icons.Default.Person,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Default.MailOutline,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CustomTextField(
                    modifier = Modifier.weight(1f),
                    value = height ,
                    onValueChange = { height=it },
                    label = "Height",
                    keyboardType = KeyboardType.Number
                )
                CustomTextField(
                    modifier = Modifier.weight(1f),
                    value = weight,
                    onValueChange = { weight=it },
                    label = "Weight",
                    keyboardType = KeyboardType.Number
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            DropdownTextField(
                label = "Surf Level",
                options = listOf("Beginner", "Intermediate", "Advanced", "Pro"),
                selectedOption = level,
                onOptionSelected = { level = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(if (isDark) OceanPalette.DarkSurface else OceanPalette.FoamWhite),
                    tint = logoTint
                )
                IconButton(
                    onClick = { /* Handle change photo */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap to upload profile pic",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            GradientButton(
                text = "Sign Up",
                onClick = { onSignUpClick() },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Already have an account? Sign in",
                modifier = Modifier.clickable { /* Navigate to login */ },
                color = logoTint,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview(showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
    )
@Composable
fun PreviewRegisterScreen() {
    QuiverSyncTheme(darkTheme = true) {
        RegisterScreen()
    }
}
