package org.example.quiversync.presentation.widgets.register

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.quiversync.domain.model.SurfLevel
import org.example.quiversync.presentation.theme.OceanPalette

@Composable
fun SurfLevelSelector(
    selectedLevel: SurfLevel?,
    onLevelSelected: (SurfLevel) -> Unit,
    errorMessage: String? = null
) {
    val isDark = isSystemInDarkTheme()
    val background = if (isDark) OceanPalette.DarkSurface else OceanPalette.FoamWhite

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            SurfLevel.entries.forEach { level ->
                val isSelected = level == selectedLevel
                ElevatedAssistChip(
                    onClick = { onLevelSelected(level) },
                    label = {
                        Text(
                            text = level.label,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = AssistChipDefaults.elevatedAssistChipColors(
                        containerColor = if (isSelected) OceanPalette.SkyBlue else background
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

    }
}
