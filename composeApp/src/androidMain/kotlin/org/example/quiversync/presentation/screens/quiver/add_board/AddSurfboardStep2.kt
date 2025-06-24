package org.example.quiversync.presentation.screens.quiver.add_board

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.example.quiversync.features.quiver.add_board.AddBoardEvent
import org.example.quiversync.features.quiver.add_board.AddBoardState
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.widgets.quiver_screen.UploadBoardImage

@Composable
fun AddSurfboardStep2(
    state: AddBoardState,
    onEvent: (AddBoardEvent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Details", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CustomTextField(
                modifier = Modifier.weight(1f),
                value = state.height,
                onValueChange = { onEvent(AddBoardEvent.HeightChanged(it)) },
                label = "Height",
            )
            CustomTextField(
                modifier = Modifier.weight(1f),
                value = state.width,
                onValueChange = { onEvent(AddBoardEvent.WidthChanged(it)) },
                label = "Width",
            )
        }
        CustomTextField(
            value = state.volume,
            onValueChange = { onEvent(AddBoardEvent.VolumeChanged(it)) },
            label = "Volume",
            keyboardType = KeyboardType.Decimal
        )
        Spacer(modifier = Modifier.height(16.dp))
        UploadBoardImage(onClick = { /* TODO: פתח בחירת תמונה והפעל onEvent(AddBoardEvent.ImageUriChanged(...)) */ })
    }

}