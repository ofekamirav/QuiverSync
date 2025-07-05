package org.example.quiversync.presentation.screens.quiver.add_board

import android.Manifest
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.example.quiversync.R
import org.example.quiversync.features.quiver.add_board.AddBoardEvent
import org.example.quiversync.features.quiver.add_board.AddBoardState
import org.example.quiversync.features.register.OnboardingEvent
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.components.ImageSourceSelectorSheet
import org.example.quiversync.presentation.widgets.register.ImagePickerSection
import org.example.quiversync.utils.extentions.toCompressedByteArray

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddSurfboardStep2(
    state: AddBoardState.Idle,
    onEvent: (AddBoardEvent) -> Unit
) {
    var showImageOptions by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
            onEvent(AddBoardEvent.surfboardImageSelected(bitmap.toCompressedByteArray()))
            Log.d("AddSurfboardStep2", "Image URI: $uri")
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            onEvent(AddBoardEvent.surfboardImageSelected(bitmap.toCompressedByteArray()))
            Log.d("AddSurfboardStep2", "Image URI: $it")
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Details", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CustomTextField(
                modifier = Modifier.weight(1f),
                value = state.data.height,
                onValueChange = { onEvent(AddBoardEvent.HeightChanged(it)) },
                label = "Height",
            )
            CustomTextField(
                modifier = Modifier.weight(1f),
                value = state.data.width,
                onValueChange = { onEvent(AddBoardEvent.WidthChanged(it)) },
                label = "Width",
            )
        }
        CustomTextField(
            value = state.data.volume,
            onValueChange = { onEvent(AddBoardEvent.VolumeChanged(it)) },
            label = "Volume",
            keyboardType = KeyboardType.Decimal
        )
        Spacer(modifier = Modifier.height(16.dp))
        ImagePickerSection(
            imageUrl = state.data.imageUrl,
            isUploading = state.data.isUploadingImage,
            onChangePhotoClick = { showImageOptions = true },
            placeholderRes = R.drawable.placeholder_dark,
            errorMessage = state.data.surfboardImageError
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