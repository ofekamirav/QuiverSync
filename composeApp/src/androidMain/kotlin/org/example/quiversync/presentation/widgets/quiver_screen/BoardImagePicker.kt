package org.example.quiversync.presentation.widgets.quiver_screen

import android.widget.Toast
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.example.quiversync.R
import org.example.quiversync.domain.model.SurfboardType
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.utils.AppConfig
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.painterResource
import org.example.quiversync.utils.extensions.getDefaultImageUrlForType

@Composable
fun BoardImagePicker(
    imageUrl: String?,
    surfboardType: SurfboardType?,
    isUploading: Boolean,
    onClick: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hasCustomImage = !imageUrl.isNullOrBlank()
    val defaultUrl = surfboardType?.let { getDefaultImageUrlForType(it) }
    val placeHolderResource = if (isSystemInDarkTheme()) {
        R.drawable.ic_board_placeholder_dark
    } else {
        R.drawable.ic_board_placeholder_light
    }

    val effectiveImageUrl = when {
        hasCustomImage -> imageUrl
        defaultUrl != null -> defaultUrl
        else -> null
    }

    Box(
        modifier = modifier
            .aspectRatio(1.6f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            isUploading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            effectiveImageUrl != null -> {
                AsyncImage(
                    model = effectiveImageUrl,
                    placeholder = painterResource(placeHolderResource) ,
                    contentDescription = "Surfboard Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                        .padding(8.dp)
                        .align(Alignment.Center)
                )
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Change Image",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clickable(onClick = onClick)
                )
            }
            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Add Surfboard Image",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap to add image",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }

        errorMessage?.let { msg ->
            LaunchedEffect(msg) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewBoardImagePickerEmpty() {
    QuiverSyncTheme {
        BoardImagePicker(imageUrl = null, isUploading = false, onClick = {},
            surfboardType = SurfboardType.SHORTBOARD, errorMessage = null)
    }
}

@Preview(showBackground = true, widthDp = 900, heightDp = 400)
@Composable
fun PreviewBoardImagePickerTablet() {
    QuiverSyncTheme {
        BoardImagePicker(
            imageUrl = "https://picsum.photos/id/237/200/300", isUploading = false, onClick = {},
            surfboardType = SurfboardType.SHORTBOARD,
            errorMessage = null,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoardImagePickerWithImage() {
    QuiverSyncTheme {
        BoardImagePicker(imageUrl = "https://picsum.photos/id/237/200/300", isUploading = false,
            onClick = {}, surfboardType = SurfboardType.SHORTBOARD, errorMessage = null)

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoardImagePickerLoading() {
    QuiverSyncTheme {
        BoardImagePicker(imageUrl = null, isUploading = true, onClick = {}, errorMessage = null, surfboardType = SurfboardType.SHORTBOARD)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoardImagePickerError() {
    QuiverSyncTheme {
        BoardImagePicker(imageUrl = null, isUploading = false, onClick = {},
            errorMessage = "Failed to upload image", surfboardType = SurfboardType.SHORTBOARD)
    }
}