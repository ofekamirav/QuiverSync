package org.example.quiversync.presentation.screens.spots

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import org.example.quiversync.R
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
    val formState by viewModel.addFavSpotState.collectAsState()

    LaunchedEffect(formState) {
        if (formState is AddFavSpotState.Loaded) {
            Toast.makeText(context, "New spot added to favorites", Toast.LENGTH_SHORT).show()
            delay(500)
            onSpotAdded()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        when (val state = formState) {
            is AddFavSpotState.Idle, is AddFavSpotState.Loading -> {
                AddSpotScreenContent(
                    viewModel = viewModel,
                    formState = state
                )
            }
            is AddFavSpotState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            is AddFavSpotState.Loaded -> {  }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSpotScreenContent(
    viewModel: AddFavSpotViewModel,
    formState: AddFavSpotState
) {
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

    var searchQuery by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val contentModifier = if (formState is AddFavSpotState.Loading) {
        Modifier.fillMaxSize().blur(8.dp)
    } else {
        Modifier.fillMaxSize()
    }

    val defaultLocation = LatLng(34.0195, -118.4912)
    var markerPosition by remember { mutableStateOf(defaultLocation) }
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    Column(
        modifier = contentModifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { /* מטופל ידנית */ },
        ) {
            val form = (formState as? AddFavSpotState.Idle)?.data
            CustomTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    viewModel.onEvent(AddFavSpotEvent.NameChanged(query))
                    if (query.length >= 2) {
                        fetchPredictions(placesClient, query, sessionToken) { newSuggestions ->
                            suggestions = newSuggestions
                            isDropdownExpanded = newSuggestions.isNotEmpty()
                        }
                    } else {
                        suggestions = emptyList()
                        isDropdownExpanded = false
                    }
                },
                label = "Enter a spot name",
                isError = form?.nameError != null,
                errorMessage = form?.nameError,
                modifier = Modifier.menuAnchor().fillMaxWidth(),
            )

            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                suggestions.forEach { prediction ->
                    DropdownMenuItem(
                        text = { Text(prediction.getFullText(null).toString()) },
                        onClick = {
                            val placeName = prediction.getFullText(null).toString()
                            val placeId = prediction.placeId
                            searchQuery = placeName
                            suggestions = emptyList()
                            isDropdownExpanded = false

                            val placeFields = listOf(Place.Field.LAT_LNG)
                            val request = FetchPlaceRequest.newInstance(placeId, placeFields)

                            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                                val latLng = response.place.latLng ?: defaultLocation
                                markerPosition = latLng
                                cameraState.position = CameraPosition.fromLatLngZoom(latLng, 12f)
                                viewModel.onEvent(AddFavSpotEvent.NameChanged(placeName))
                                viewModel.onEvent(AddFavSpotEvent.LocationChanged(latLng.latitude, latLng.longitude))
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp)
        ) {
            MapWithCustomSvgMarker(
                modifier = Modifier.fillMaxSize(),
                position = markerPosition,
                cameraPositionState = cameraState,
                onMapClick = { latLng ->
                    markerPosition = latLng
                    viewModel.onEvent(AddFavSpotEvent.LocationChanged(latLng.latitude, latLng.longitude))
                },
                markerSvgRes = R.drawable.ic_marker
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GradientButton(
            text = "Add Spot",
            onClick = { viewModel.onEvent(AddFavSpotEvent.SaveClicked) },
            modifier = Modifier.fillMaxWidth(),
            enabled = formState !is AddFavSpotState.Loading,
            shape = RoundedCornerShape(12.dp),
        )
    }

    if (formState is AddFavSpotState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            LoadingAnimation(
                isLoading = true,
                animationFileName = "quiver_sync_loading_animation.json",
                animationSize = 240.dp
            )
        }
    }
}

private fun fetchPredictions(
    placesClient: com.google.android.libraries.places.api.net.PlacesClient,
    query: String,
    sessionToken: AutocompleteSessionToken,
    onResult: (List<AutocompletePrediction>) -> Unit
) {
    val request = FindAutocompletePredictionsRequest.builder()
        .setSessionToken(sessionToken)
        .setQuery(query)
        .build()
    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response -> onResult(response.autocompletePredictions) }
        .addOnFailureListener { onResult(emptyList()) }
}