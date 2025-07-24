package org.example.quiversync.presentation.widgets.register

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme

@Composable
fun ImagePickerSection(
    imageUrl: String?,
    isUploading: Boolean,
    onChangePhotoClick: () -> Unit,
    placeholderRes: Int,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center) {
        if (isUploading) {
            CircularProgressIndicator(modifier = Modifier.size(60.dp), color = MaterialTheme.colorScheme.primary)
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile Picture",
                placeholder = painterResource(placeholderRes),
                error = painterResource(placeholderRes),
                fallback = painterResource(placeholderRes),
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .clip(CircleShape)
            )
            IconButton(
                onClick = onChangePhotoClick,
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


        if (errorMessage != null) {
            // Display error message
            Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
