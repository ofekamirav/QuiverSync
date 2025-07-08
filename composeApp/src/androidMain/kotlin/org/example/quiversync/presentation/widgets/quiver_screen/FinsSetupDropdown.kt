package org.example.quiversync.presentation.widgets.quiver_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.quiversync.domain.model.FinsSetup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinsSetupDropdown(
    selected: FinsSetup?,
    onSelected: (FinsSetup) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected?.serverName ?: "",
            onValueChange = {},
            readOnly = true,
            modifier = modifier
                .menuAnchor()
                .clickable { expanded = true },
            label = { Text("Fins Setup") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.onBackground,
                unfocusedTextColor = colors.onBackground,
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.outline,
                cursorColor = colors.primary,
                focusedLabelColor = colors.primary,
                unfocusedLabelColor = colors.onSurfaceVariant,
                focusedContainerColor = colors.surface.copy(alpha = 0f),
                unfocusedContainerColor = colors.surface.copy(alpha = 0f),
                disabledContainerColor = colors.surface.copy(alpha = 0f),
                errorContainerColor = colors.surface.copy(alpha = 0f),
                errorBorderColor = colors.error,
                errorLabelColor = colors.error,
            ),
            isError = isError,
            supportingText = {
                if (isError && !errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        color = colors.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = colors.surface,
        ) {
            FinsSetup.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.serverName) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
