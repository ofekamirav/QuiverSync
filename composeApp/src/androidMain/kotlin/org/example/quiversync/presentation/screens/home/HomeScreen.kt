package org.example.quiversync.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.quiversync.presentation.theme.QuiverSyncTheme
import org.example.quiversync.presentation.widgets.home_screen.ForecastPanel
import org.example.quiversync.presentation.widgets.home_screen.MainConditions
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    val windowInfo = LocalWindowInfo.current

    when (windowInfo.widthSize) {
        WindowWidthSize.COMPACT -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                MainConditions(expanded = expanded, onExpandToggle = { expanded = !expanded })
                ForecastPanel()
            }
        }

        else -> { // MEDIUM or EXPANDED
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(0.6f)) {
                    MainConditions(expanded = expanded, onExpandToggle = { expanded = !expanded })
                }
                Column(modifier = Modifier.weight(0.4f)) {
                    ForecastPanel()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    QuiverSyncTheme() {
        HomeScreen()
    }
}
