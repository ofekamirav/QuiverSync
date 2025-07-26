package org.example.quiversync.presentation.screens.spots

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import org.example.quiversync.R
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import org.example.quiversync.features.spots.add_fav_spot.AddFavSpotEvent
import org.example.quiversync.features.spots.add_fav_spot.AddFavSpotState
import org.example.quiversync.features.spots.add_fav_spot.AddFavSpotViewModel
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.components.GradientButton
import org.example.quiversync.presentation.components.LoadingAnimation
import org.example.quiversync.presentation.widgets.spots_screen.MapWithCustomSvgMarker
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpotScreen(
    modifier: Modifier = Modifier,
    viewModel: AddFavSpotViewModel = koinViewModel(),
    onSpotAdded: () -> Unit
) {
    val context = LocalContext.current

    val placesClient = remember { Places.createClient(context) }
    val session    = remember { AutocompleteSessionToken.newInstance() }

    val formState by viewModel.addFavSpotState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var expanded   by remember { mutableStateOf(false) }

    val defaultLocation = LatLng(34.0195, -118.4912)
    var markerPosition by remember { mutableStateOf(defaultLocation) }
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }
    val contentModifier = if (formState is AddFavSpotState.Loading) {
        modifier.blur(radius = 8.dp)
    } else {
        modifier
    }

    LaunchedEffect(formState) {
        if (formState is AddFavSpotState.Loaded) {
            Toast.makeText(context, "New spot added to favorites", Toast.LENGTH_SHORT).show()
            delay(1500)
            onSpotAdded()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (formState) {
            is AddFavSpotState.Loaded -> {
                // This state is handled in LaunchedEffect
            }
            is AddFavSpotState.Error -> {
                Text(
                    text = (formState as AddFavSpotState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
            is AddFavSpotState.Idle, is AddFavSpotState.Loading -> {
                val form = (formState as? AddFavSpotState.Idle)?.data
                Box(
                    modifier = contentModifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween

                    ) {
                        ExposedDropdownMenuBox(
                            expanded = searchQuery.length >= 2,
                            onExpandedChange = {},
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CustomTextField(
                                value = searchQuery,
                                onValueChange = { q ->
                                    searchQuery = q
                                    viewModel.onEvent(AddFavSpotEvent.NameChanged(q))
                                    if (q.length >= 2) {
                                        fetchPredictions(
                                            placesClient,
                                            q,
                                            session
                                        ) { suggestionsList ->
                                            suggestions = suggestionsList
                                            expanded = suggestionsList.isNotEmpty()
                                        }
                                    } else {
                                        suggestions = emptyList()
                                        expanded = false
                                    }
                                },
                                label = "Enter a spot name",
                                isError = form?.nameError != null,
                                errorMessage = form?.nameError,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = false
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                suggestions.forEach { prediction ->
                                    DropdownMenuItem(
                                        text = { Text(prediction.getFullText(null).toString()) },
                                        onClick = {
                                            val placeName = prediction.getFullText(null).toString()
                                            val fetchRequest = FetchPlaceRequest.builder(
                                                prediction.placeId,
                                                listOf(Place.Field.LAT_LNG)
                                            ).build()
                                            placesClient.fetchPlace(fetchRequest)
                                                .addOnSuccessListener { place ->
                                                    val latLng =
                                                        place.place.location ?: defaultLocation
                                                    searchQuery = placeName
                                                    suggestions = emptyList()
                                                    expanded = false
                                                    markerPosition = latLng
                                                    cameraState.position =
                                                        CameraPosition.fromLatLngZoom(latLng, 12f)

                                                    viewModel.onEvent(
                                                        AddFavSpotEvent.NameChanged(
                                                            placeName
                                                        )
                                                    )
                                                    viewModel.onEvent(
                                                        AddFavSpotEvent.LocationChanged(
                                                            latitude = latLng.latitude,
                                                            longitude = latLng.longitude
                                                        )
                                                    )
                                                }
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(30.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            MapWithCustomSvgMarker(
                                position = markerPosition,
                                cameraPositionState = cameraState,
                                onMapClick = { latLng ->
                                    markerPosition = latLng
                                    viewModel.onEvent(
                                        AddFavSpotEvent.LocationChanged(
                                            latitude = latLng.latitude,
                                            longitude = latLng.longitude
                                        )
                                    )
                                },
                                markerSvgRes = R.drawable.ic_marker
                            )
                        }
                        form?.locationError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        GradientButton(
                            text = "Add Spot",
                            onClick = { viewModel.onEvent(AddFavSpotEvent.SaveClicked) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = formState !is AddFavSpotState.Loading
                        )
                    }
                }
            }
        }

        if (formState is AddFavSpotState.Loading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(
                    isLoading          = true,
                    animationFileName  = "quiver_sync_loading_animation.json",
                    animationSize      = 240.dp
                )
            }
        }
    }
}

private fun fetchPredictions(
    placesClient: PlacesClient,
    query: String,
    session: AutocompleteSessionToken,
    onResult: (List<AutocompletePrediction>) -> Unit
) {
    val request = FindAutocompletePredictionsRequest.builder()
        .setSessionToken(session)
        .setQuery(query)
        .build()
    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { resp ->
            onResult(resp.autocompletePredictions)
        }
        .addOnFailureListener {
            onResult(emptyList())
        }
}
