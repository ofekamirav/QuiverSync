package org.example.quiversync.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.presentation.theme.QuiverSyncTheme
@Composable
fun RentalPublishDialog(
    board: String,
    onDismiss: () -> Unit,
    onConfirm: (price: Double) -> Unit
) {
    var priceInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val performConfirm = {
        val price = priceInput.toDoubleOrNull()
        if (price != null && price > 0) {
            onConfirm(price)
        } else {
            isError = true
        }
    }

    AlertDialog(
        modifier = Modifier.widthIn(min = 300.dp, max = 500.dp),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismiss,
        title = { Text(text = "Publish ${board}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary) },
        text = {
            Column {
                Text("Set your daily rental price below.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    value = priceInput,
                    onValueChange = {
                        priceInput = it
                        isError = false
                    },
                    label = "Price per day ($)",
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    isError = isError,
                    errorMessage = if (isError) "Please enter a valid price" else null
                )
            }
        },
        confirmButton = {
            TextButton(onClick = performConfirm) {
                Text(
                    "Publish",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun RentalPublishDialogPreview(){
    QuiverSyncTheme {
        RentalPublishDialog(
            board = "HolyGrail",
            onDismiss = { /*TODO*/ },
            onConfirm = { /*TODO*/ }
        )
    }
}