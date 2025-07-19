package org.example.quiversync.presentation.screens.register

import android.Manifest
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.android.volley.toolbox.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import org.example.quiversync.domain.model.OnboardingProfileDetails
import org.example.quiversync.features.register.OnboardingState
import org.example.quiversync.features.register.OnboardingViewModel
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.components.DateOfBirthPicker
import org.example.quiversync.presentation.components.GradientButton
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.presentation.widgets.register.SurfLevelSelector
import org.koin.androidx.compose.koinViewModel
import org.example.quiversync.R
import org.example.quiversync.features.register.OnboardingEvent
import org.example.quiversync.features.register.OnboardingFormData
import org.example.quiversync.presentation.components.ImageSourceSelectorSheet
import org.example.quiversync.presentation.widgets.register.ImagePickerSection
import org.example.quiversync.utils.extentions.toCompressedByteArray


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = koinViewModel(),
    onCompleteClick:  () -> Unit,
) {
    val uiState by viewModel.onboardingState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (val currentState = uiState) {
        is OnboardingState.Idle -> {
            CompleteRegisterScreen(
                formData = currentState.data,
                onEvent = viewModel::onEvent,
            )
        }
        is OnboardingState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(isLoading = true, animationFileName = "quiver_sync_loading_animation.json", animationSize = 240.dp)
            }

        }
        is OnboardingState.Success -> {
            LaunchedEffect(Unit) {
                onCompleteClick()
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                viewModel.resetStateToIdle()
            }
        }
        is OnboardingState.Error -> {
            val errorMessage = (uiState as OnboardingState.Error).message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CompleteRegisterScreen(
    formData: OnboardingFormData,
    onEvent: (OnboardingEvent) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val isDark = isSystemInDarkTheme()
    val placeholderRes = if (isDark) R.drawable.placeholder_dark else R.drawable.placeholder_light
    val coroutineScope = rememberCoroutineScope()
    var showTermsDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val termsText = remember {
        context.resources.openRawResource(R.raw.terms_of_service)
            .bufferedReader().use { it.readText() }
    }

    val termsAnnotatedString = remember {
        buildAnnotatedString {
            append("I agree to the ")
            pushStringAnnotation(tag = "TERMS_CLICKABLE", annotation = "terms_url_or_action")
            withStyle(style = SpanStyle(color = primaryColor)) {
                append("Terms of Service and Privacy Policy")
            }
            pop()
        }
    }

    var showImageOptions by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val storagePermissionState = rememberPermissionState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
            onEvent(OnboardingEvent.ProfileImageSelected(bitmap.toCompressedByteArray()))
            Log.d("OnboardingScreen", "Image URI: $uri")
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            onEvent(OnboardingEvent.ProfileImageSelected(bitmap.toCompressedByteArray()))
            Log.d("OnboardingScreen", "Image URI: $it")
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
                         onEvent(OnboardingEvent.ContinueClicked)
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

                CustomTextField(
                    value = formData.phoneNumber,
                    onValueChange = { onEvent(OnboardingEvent.PhoneNumberChanged(it)) },
                    label = "Phone Number",
                    isError = formData.phoneNumberError != null,
                    errorMessage = formData.phoneNumberError,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(16.dp))

                DateOfBirthPicker(
                    selectedDate = formData.dateOfBirth,
                    onDateSelected = { onEvent(OnboardingEvent.DateOfBirthChanged(it)) },
                    errorMessage = formData.dateOfBirthError
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = formData.heightCm,
                    onValueChange = { onEvent(OnboardingEvent.HeightChanged(it)) },
                    label = "Height (cm)",
                    isError = formData.heightError != null,
                    errorMessage = formData.heightError,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    value = formData.weightKg,
                    onValueChange = { onEvent(OnboardingEvent.WeightChanged(it)) },
                    label = "Weight (kg)",
                    isError = formData.weightError != null,
                    errorMessage = formData.weightError,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "How do you rate your surfing?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                formData.selectedSurfLevel?.let {
                    SurfLevelSelector(
                        selectedLevel = it,
                        onLevelSelected = { onEvent(OnboardingEvent.SurfLevelChanged(it)) },
                        errorMessage = formData.surfLevelError
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                ImagePickerSection(
                    imageUrl = formData.profileImageUrl,
                    isUploading = formData.isUploadingImage,
                    onChangePhotoClick = { showImageOptions = true },
                    placeholderRes = placeholderRes,
                    errorMessage = formData.profileImageError
                )

                Spacer(modifier = Modifier.height(12.dp))


                Text(
                    text = "Tap to upload profile pic",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Checkbox(
                        checked = formData.agreedToTerms,
                        onCheckedChange = { onEvent(OnboardingEvent.OnAgreementChange(it)) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = Color.Gray
                        )
                    )
                    ClickableText(
                        text = termsAnnotatedString,
                        onClick = { offset ->
                            termsAnnotatedString.getStringAnnotations(
                                tag = "TERMS_CLICKABLE",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let {
                                showTermsDialog = true
                            } ?: run {
                                onEvent(OnboardingEvent.OnAgreementChange(!formData.agreedToTerms))
                            }
                        },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                    )
                    formData.termsError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
            }
            if (showTermsDialog) {
                AlertDialog(
                    onDismissRequest = { showTermsDialog = false },
                    title = { Text("Terms of Service") },
                    text = {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = termsText,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showTermsDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }
            if (showImageOptions) {
                ImageSourceSelectorSheet(
                    onDismiss = { showImageOptions = false },
                    onTakePhoto = {
                        showImageOptions = false
                        if (cameraPermissionState.status.isGranted) {
                            cameraLauncher.launch(null)
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                            if (cameraPermissionState.status.isGranted) {
                                cameraLauncher.launch(null)
                            }
                        }
                    },
                    onChooseFromGallery = {
                        showImageOptions = false
                        if (storagePermissionState.status.isGranted) {
                            galleryLauncher.launch("image/*")
                        } else {
                            storagePermissionState.launchPermissionRequest()
                            if (storagePermissionState.status.isGranted) {
                                galleryLauncher.launch("image/*")
                            }
                        }
                    }
                )
            }
        }
    )
}
