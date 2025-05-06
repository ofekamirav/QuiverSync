package com.example.quiversync.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quiversync.android.R
import com.example.quiversync.android.navigation.RootNavGraph
import com.ramcosta.composedestinations.annotation.Destination
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition

@RootNavGraph
@Destination<RootNavGraph>
@Composable
fun ForecastScreen() {
    val defaultLocation = LatLng(34.0195, -118.4912) // Santa Monica
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf9fafb))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Search a Surf Spot",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3366FF)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                painter = painterResource(id = R.drawable.quiversync_logo),
                contentDescription = "Logo",
                tint = Color(0xFF007BFF),
                modifier = Modifier.size(30.dp)
            )
        }


        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Enter a beach, break, or city") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF3366FF)
            )
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(30.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = defaultLocation),
                    title = "Santa Monica"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForecastScreenPreview() {
    MaterialTheme {
        ForecastScreen()
    }
}
