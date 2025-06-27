package org.example.quiversync.presentation.components

import androidx.compose.material3.DatePickerDialog
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import org.example.quiversync.presentation.theme.OceanPalette
import java.util.Date
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateOfBirthPicker(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    errorMessage: String? = null
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState()

    Column {
        CustomTextField(
            value = selectedDate,
            onValueChange = { },
            label = "Date of Birth",
            imeAction = ImeAction.Next,
            readOnly = true,
            onClick = { isDialogOpen = true },
            isError = errorMessage != null,
            errorMessage = errorMessage,
            leadingIcon = Icons.Default.DateRange,
            enabled = false,
        )

        if (isDialogOpen) {
            DatePickerDialog(
                onDismissRequest = { isDialogOpen = false },
                confirmButton = {
                    TextButton(onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(millis))
                            onDateSelected(formattedDate)
                        }
                        isDialogOpen = false
                    }) {
                        Text("OK", color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = if(isSystemInDarkTheme()) OceanPalette.DarkSurface else Color.White,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                dismissButton = {
                    TextButton(onClick = { isDialogOpen = false }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            ) {
                DatePicker(
                    state = dateState,
                    showModeToggle = false,
                    colors =  DatePickerDefaults.colors(
                        containerColor = if(isSystemInDarkTheme()) OceanPalette.DarkSurface else Color.White,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        headlineContentColor = MaterialTheme.colorScheme.onSurface,
                        weekdayContentColor = MaterialTheme.colorScheme.onSurface,
                        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                        dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        dayContentColor = MaterialTheme.colorScheme.onSurface,
                        todayContentColor = MaterialTheme.colorScheme.primary,
                        todayDateBorderColor = MaterialTheme.colorScheme.primary,
                        subheadContentColor = MaterialTheme.colorScheme.onSurface,
                    )
                )
            }
        }
    }
}


