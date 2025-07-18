package org.example.quiversync.presentation.screens.settings

import android.Manifest
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.example.quiversync.R
import org.example.quiversync.features.register.OnboardingEvent
import org.example.quiversync.features.user.edit_user.EditProfileDetailsViewModel
import org.example.quiversync.features.user.edit_user.EditUserDetailsEvent
import org.example.quiversync.features.user.edit_user.EditUserFormData
import org.example.quiversync.features.user.edit_user.EditUserState
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.components.GradientButton
import org.example.quiversync.presentation.components.ImageSourceSelectorSheet
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.presentation.screens.skeletons.EditProfileSkeleton
import org.example.quiversync.presentation.widgets.register.ImagePickerSection
import org.example.quiversync.presentation.widgets.register.SurfLevelSelector
import org.example.quiversync.utils.extensions.toSurfLevel
import org.example.quiversync.utils.extentions.toCompressedByteArray
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditProfileDetailsScreen(
    viewModel: EditProfileDetailsViewModel = koinViewModel(),
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val saveLoading by viewModel.saveLoading.collectAsState()

    when (uiState) {
        is EditUserState.Loading -> {
            EditProfileSkeleton(modifier = modifier)
        }
        is EditUserState.Editing -> {
            EditProfileDetailsScreenContent(
                modifier = modifier,
                data = (uiState as EditUserState.Editing).form,
                onSave = onSave,
                onEvent = viewModel::onEvent,
            )
            if (saveLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation(isLoading = true, animationFileName = "quiver_sync_loading_animation.json", animationSize = 240.dp)
                }
            }
        }
        is EditUserState.Success -> {
            onSave()
        }
        is EditUserState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (uiState as EditUserState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditProfileDetailsScreenContent(
    data: EditUserFormData,
    onSave: () -> Unit,
    onEvent: (EditUserDetailsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val placeholder = if (isDark) R.drawable.placeholder_dark else R.drawable.placeholder_light
    val context = LocalContext.current
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
            onEvent(EditUserDetailsEvent.ProfileImageSelected(bitmap.toCompressedByteArray()))
            Log.d("OnboardingScreen", "Image URI: $uri")
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            onEvent(EditUserDetailsEvent.ProfileImageSelected(bitmap.toCompressedByteArray()))
            Log.d("OnboardingScreen", "Image URI: $it")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagePickerSection(
            imageUrl = data.profilePicture,
            isUploading = data.isUploadingImage,
            onChangePhotoClick = { showImageOptions = true },
            placeholderRes = placeholder,
            errorMessage = data.profilePictureError
        )
        CustomTextField(
            value = data.name ?: "",
            onValueChange = { onEvent(EditUserDetailsEvent.onNameChange(it)) },
            label = "Name",
            modifier = Modifier.fillMaxWidth()
        )
        CustomTextField(
            value = data.height.toString(),
            onValueChange = { onEvent(EditUserDetailsEvent.onHeightChange(it.toDouble())) },
            label = "Height(In)",
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Number
        )
        CustomTextField(
            value = data.weight.toString(),
            onValueChange = { onEvent(EditUserDetailsEvent.onWeightChange(it.toDouble())) },
            label = "Weight(kg)",
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth()
        )
        data.surfLevel?.let {
            SurfLevelSelector(
                selectedLevel = it.toSurfLevel(),
                onLevelSelected = { onEvent(EditUserDetailsEvent.onSurfLevelChange(it.name)) },
            )
        }
        Spacer(modifier = Modifier.weight(1f))


        GradientButton(
            text = "Save Changes",
            onClick = {
                onEvent(EditUserDetailsEvent.onSubmit)
                onSave()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        )
        if (showImageOptions) {
            ImageSourceSelectorSheet(
                onDismiss = { showImageOptions = false },
                onTakePhoto = {
                    showImageOptions = false
                    if (cameraPermissionState.status.isGranted) {
                        cameraLauncher.launch(null)
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                },
                onChooseFromGallery = {
                    showImageOptions = false
                    if (storagePermissionState.status.isGranted) {
                        galleryLauncher.launch("image/*")
                    } else {
                        storagePermissionState.launchPermissionRequest()
                    }
                }
            )
        }
    }
}