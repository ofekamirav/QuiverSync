package org.example.quiversync.presentation.screens.register

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.OnboardingProfileDetails
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.components.DateOfBirthPicker
import org.example.quiversync.presentation.components.GradientButton
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.widgets.register.SurfLevel
import org.example.quiversync.presentation.widgets.register.SurfLevelSelector
import org.example.quiversync.utils.FirebaseStorageUploader
import org.example.quiversync.utils.toCompressedByteArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteRegisterScreen(
    onCompleteClick: (OnboardingProfileDetails) -> Unit = {},
) {
    var dateOfBirth by remember { mutableStateOf ("") }
    var heightCm by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var selectedSurfLevel by remember { mutableStateOf<SurfLevel?>(null) }
    val isDark = isSystemInDarkTheme()
    val logoTint = if (isDark) OceanPalette.SkyBlue else OceanPalette.DeepBlue
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUploading by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        uri?.let {
            imageUploading = true
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }

            val uploader = FirebaseStorageUploader()
            coroutineScope.launch {
                val result  = uploader.uploadImage(
                    imageBytes = bitmap.toCompressedByteArray(),
                    name = "profile_${System.currentTimeMillis()}",
                    folder = "profile_pictures",
                    onSuccess = {
                        imageUrl = it
                        imageUploading = false
                    },
                    onError = {
                        Log.e("Upload", "Error: $it")
                        imageUploading = false
                    }
                )
            }

        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Personalize Your Experience") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth(),
            )
        },
        bottomBar = {
            Box(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GradientButton(
                    text = "Continue",
                    onClick = {
                        if (selectedSurfLevel != null) {
                            onCompleteClick(
                                OnboardingProfileDetails(
                                    dateOfBirth = dateOfBirth,
                                    heightCm = heightCm,
                                    weightKg = weightKg,
                                    surfLevel = selectedSurfLevel!!.label
                                )
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "This helps us find the best spots and gear for you.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                LinearProgressIndicator(
                    progress = 1.0f,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                DateOfBirthPicker(
                    selectedDate = dateOfBirth,
                    onDateSelected = { dateOfBirth = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = heightCm,
                    onValueChange = { heightCm = it },
                    label = "Height (cm)",
                    imeAction = ImeAction.Next,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    value = weightKg,
                    onValueChange = { weightKg = it },
                    label = "Weight (kg)",
                    imeAction = ImeAction.Next,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "How do you rate your surfing?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SurfLevelSelector(
                    selectedLevel = selectedSurfLevel,
                    onLevelSelected = { selectedSurfLevel = it }
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
                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Tap to upload profile pic",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewOnboardingScreenNoScaffold() {
    QuiverSyncTheme {
        CompleteRegisterScreen()
    }
}