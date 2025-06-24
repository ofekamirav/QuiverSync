package org.example.quiversync.presentation.screens.quiver.add_board

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.domain.model.SurfboardType
import org.example.quiversync.features.quiver.add_board.AddBoardEvent
import org.example.quiversync.features.quiver.add_board.AddBoardState
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.widgets.quiver_screen.SelectableBoardTypeGrid

@Composable
fun AddSurfboardStep1(state: AddBoardState, onEvent: (AddBoardEvent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CustomTextField(
            value = state.model,
            onValueChange = { onEvent(AddBoardEvent.ModelChanged(it)) },
            label = "Model Name",
            imeAction = ImeAction.Next
        )
        CustomTextField(
            value = state.company,
            onValueChange = { onEvent(AddBoardEvent.CompanyChanged(it)) },
            label = "Company / Shaper",
            imeAction = ImeAction.Done
        )
        Text("Select surfboard type", style = MaterialTheme.typography.titleMedium)
        SelectableBoardTypeGrid(
            selectedType = state.boardType,
            onTypeSelected = { selected -> onEvent(AddBoardEvent.BoardTypeChanged(selected)) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddSurfboardStep1Preview() {
    // Create a sample AddBoardState for previewing
    val sampleState = AddBoardState(
        model = "Hypto Krypto",
        company = "Haydenshapes",
        boardType = SurfboardType.SHORTBOARD
    )

    // Call your Composable with the sample state and a no-op event handler
    AddSurfboardStep1(state = sampleState, onEvent = {})
}