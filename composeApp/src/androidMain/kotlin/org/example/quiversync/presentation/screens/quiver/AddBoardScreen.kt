package org.example.quiversync.presentation.screens.quiver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.widgets.quiver_screen.SurfboardTypeDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBoardScreen() {
    val focusManager = LocalFocusManager.current

    var model by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var boardType by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var volume by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomTextField(
            value = model,
            onValueChange = { model = it },
            label = "Model",
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        CustomTextField(
            value = company,
            onValueChange = { company = it },
            label = "Company",
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        SurfboardTypeDropdown(
            selectedType = boardType,
            onTypeSelected = { boardType = it },
            modifier = Modifier.fillMaxWidth()
        )

        CustomTextField(
            value = height,
            onValueChange = { height = it },
            label = "Height",
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        CustomTextField(
            value = width,
            onValueChange = { width = it },
            label = "Width",
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        CustomTextField(
            value = volume,
            onValueChange = { volume = it },
            label = "Volume",
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            onImeAction = { focusManager.clearFocus() }
        )

        UploadImageBox(onClick = { /* TODO */ })

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { /* TODO: Add surfboard */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondary)
        ) {
            Text("Add Surfboard", color = Color.White)
        }
    }
}

@Composable
fun UploadImageBox(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, OceanPalette.BorderGray, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Upload Image",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tap to upload an image of your surfboard",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondary)
            ) {
                Text("Upload", color = OceanPalette.DeepBlue)
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AddBoardScreenPreview() {
    QuiverSyncTheme(darkTheme = true) {
        AddBoardScreen()
    }

}
