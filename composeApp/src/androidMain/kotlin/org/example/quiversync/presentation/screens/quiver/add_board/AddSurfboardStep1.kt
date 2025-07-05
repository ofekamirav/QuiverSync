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
import org.example.quiversync.presentation.widgets.quiver_screen.FinsSetupDropdown
import org.example.quiversync.presentation.widgets.quiver_screen.SelectableBoardTypeGrid

@Composable
fun AddSurfboardStep1(state: AddBoardState.Idle, onEvent: (AddBoardEvent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CustomTextField(
            value = state.data.model,
            onValueChange = { onEvent(AddBoardEvent.ModelChanged(it)) },
            label = "Model Name",
            imeAction = ImeAction.Next,
            isError = state.data.modelError != null,
            errorMessage = state.data.modelError
        )
        CustomTextField(
            value = state.data.company,
            onValueChange = { onEvent(AddBoardEvent.CompanyChanged(it)) },
            label = "Company / Shaper",
            imeAction = ImeAction.Done,
            isError = state.data.companyError != null,
            errorMessage = state.data.companyError
        )
        Text("Select fins ", style = MaterialTheme.typography.titleMedium)
        FinsSetupDropdown(
            selected = state.data.finSetup,
            onSelected = { onEvent(AddBoardEvent.FinsSetupChanged(it)) }
        )
        Text("Select surfboard type", style = MaterialTheme.typography.titleMedium)
        SelectableBoardTypeGrid(
            selectedType = state.data.boardType,
            onTypeSelected = { selected -> onEvent(AddBoardEvent.BoardTypeChanged(selected)) }
        )
    }
}