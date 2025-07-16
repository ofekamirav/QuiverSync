package org.example.quiversync.presentation.widgets.spots_screen

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.core.graphics.createBitmap
import com.google.maps.android.compose.CameraPositionState

@Composable
fun MapWithCustomSvgMarker(
    position: LatLng,
    cameraPositionState: CameraPositionState,
    onMapClick: (LatLng) -> Unit,
    @DrawableRes markerSvgRes: Int,
) {
    val context = LocalContext.current
    var bitmapDescriptor: BitmapDescriptor? by remember { mutableStateOf(null) }
    val desiredWidth = 80
    val desiredHeight = 102

    LaunchedEffect(context, markerSvgRes) {
        val drawable = ContextCompat.getDrawable(context, markerSvgRes)
        if (drawable != null) {
            val bitmap = createBitmap(desiredWidth,desiredHeight)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            onMapClick(latLng)
        }
    ) {
        bitmapDescriptor?.let { icon ->
            Marker(
                state = MarkerState(position),
                title = "Selected Location",
                icon = icon,
                snippet = "Your Selected Spot",
            )
        }
    }
}