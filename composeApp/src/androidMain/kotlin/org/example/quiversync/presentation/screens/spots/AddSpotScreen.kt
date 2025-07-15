package org.example.quiversync.presentation.screens.spots

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.example.quiversync.features.spots.AddFavSpot.AddFavSpotEvent
import org.example.quiversync.features.spots.AddFavSpot.AddFavSpotState
import org.example.quiversync.features.spots.AddFavSpot.AddFavSpotViewModel
import org.example.quiversync.presentation.components.GradientButton
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.koin.androidx.compose.koinViewModel
@Composable
fun AddSpotScreen(
    modifier: Modifier = Modifier,
    viewModel: AddFavSpotViewModel = koinViewModel(),
    onSpotAdded: () -> Unit
) {
    // Observe view state
    val uiState by viewModel.addFavSpotState.collectAsState()

    // Local state for camera & marker
    val defaultLocation = LatLng(34.0195, -118.4912)
    val markerPosition = remember { mutableStateOf(defaultLocation) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    // When save succeeds, navigate away
    LaunchedEffect(uiState) {
        if (uiState is AddFavSpotState.Loaded) {
            onSpotAdded()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is AddFavSpotState.Loaded -> {
                Toast.makeText(LocalContext.current, "New spot added to favorites", Toast.LENGTH_SHORT).show()
            }
            is AddFavSpotState.Loading -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingAnimation(isLoading = true, animationFileName = "quiver_sync_loading_animation.json", animationSize = 240.dp)
                }
            }
            is AddFavSpotState.Error -> {
                Text(
                    text = (uiState as AddFavSpotState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
            is AddFavSpotState.Idle -> {
                val form = (uiState as AddFavSpotState.Idle).data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Name input
                    OutlinedTextField(
                        value = form.name,
                        onValueChange = { viewModel.onEvent(AddFavSpotEvent.NameChanged(it)) },
                        isError = form.nameError != null,
                        placeholder = { Text("Enter a spot name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.LightGray,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    form.nameError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    // Map picker
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(30.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            onMapClick = { latLng ->
                                markerPosition.value = latLng
                                // dispatch location change
                                viewModel.onEvent(
                                    AddFavSpotEvent.LocationChanged(
                                        latitude = latLng.latitude,
                                        longitude = latLng.longitude
                                    )
                                )
                            }
                        ) {
                            Marker(
                                state = MarkerState(position = markerPosition.value),
                                title = "Pick your spot"
                            )
                        }
                    }
                    form.locationError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    GradientButton(
                        text = "Add Spot",
                        onClick = { viewModel.onEvent(AddFavSpotEvent.SaveClicked) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                    )
                }
            }
        }
    }
}
