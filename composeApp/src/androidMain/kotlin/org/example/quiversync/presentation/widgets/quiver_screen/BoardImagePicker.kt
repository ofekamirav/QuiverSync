package org.example.quiversync.presentation.components // שינוי מיקום החבילה ל-components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn // ייבוא חדש לטאבלט
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.example.quiversync.R
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize


@Composable
fun BoardImagePicker(
    imageUrl: String?,
    isUploading: Boolean,
    onClick: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val showPlaceholder = imageUrl.isNullOrEmpty()
    val isDark = isSystemInDarkTheme()
    val windowInfo = LocalWindowInfo.current
    val context = LocalContext.current

    val boxModifier = if (windowInfo.widthSize == WindowWidthSize.COMPACT) {
        modifier.fillMaxWidth().height(200.dp)
    } else {
        modifier.widthIn(min = 250.dp, max = 350.dp).height(250.dp)
    }
    val backgroundColor = if (isDark) OceanPalette.DarkCard else OceanPalette.FoamWhite
    val borderColor = if (isDark) OceanPalette.SkyBlue else OceanPalette.DeepBlue
    val iconTint = if (isDark) OceanPalette.SkyBlue.copy(alpha = 0.7f) else OceanPalette.DeepBlue.copy(alpha = 0.7f)
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = boxModifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isUploading) {
            CircularProgressIndicator(modifier = Modifier.size(60.dp), color = MaterialTheme.colorScheme.primary)
        } else if (showPlaceholder) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Add Surfboard Image",
                    tint = iconTint,
                    modifier = Modifier.size(if (windowInfo.widthSize == WindowWidthSize.COMPACT) 48.dp else 64.dp) // גודל אייקון מותאם לטאבלט
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Click to add a surfboard image",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Selected Surfboard Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (errorMessage != null) {
            LaunchedEffect(errorMessage) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoardImagePickerEmpty() {
    QuiverSyncTheme {
        BoardImagePicker(imageUrl = null, isUploading = false, onClick = {})
    }
}

@Preview(showBackground = true, widthDp = 900, heightDp = 400) // Preview לטאבלט
@Composable
fun PreviewBoardImagePickerTablet() {
    QuiverSyncTheme {
        BoardImagePicker(imageUrl = "https://picsum.photos/id/237/200/300", isUploading = false, onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoardImagePickerWithImage() {
    QuiverSyncTheme {
        BoardImagePicker(imageUrl = "https://picsum.photos/id/237/200/300", isUploading = false, onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoardImagePickerLoading() {
    QuiverSyncTheme {
        BoardImagePicker(imageUrl = null, isUploading = true, onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBoardImagePickerError() {
    QuiverSyncTheme {
        BoardImagePicker(imageUrl = null, isUploading = false, onClick = {}, errorMessage = "שגיאה בטעינת תמונה!")
    }
}