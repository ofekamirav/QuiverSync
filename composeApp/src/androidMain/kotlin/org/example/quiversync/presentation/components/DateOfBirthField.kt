package org.example.quiversync.presentation.components

import androidx.compose.material3.DatePickerDialog
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun DateOfBirthPicker(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState()

    Column {
        CustomTextField(
            value = selectedDate,
            onValueChange = {},
            label = "Date of Birth",
            imeAction = ImeAction.Next,
            readOnly = true,
            onClick = { isDialogOpen = true }
        )

        if (isDialogOpen) {
            DatePickerDialog(
                onDismissRequest = { isDialogOpen = false },
                confirmButton = {
                    TextButton(onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
                            onDateSelected(formattedDate)
                        }
                        isDialogOpen = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isDialogOpen = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = dateState)
            }
        }
    }
}

