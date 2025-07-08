package org.example.quiversync.presentation.widgets.quiver_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.presentation.components.CustomTextField
import org.example.quiversync.presentation.components.GradientButton
import org.example.quiversync.presentation.theme.OceanPalette
import org.example.quiversync.utils.LocalWindowInfo
import org.example.quiversync.utils.WindowWidthSize

@Composable
fun PublishSurfboardDialog(
    surfboard: Surfboard,
    visible: Boolean,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    var pricePerDay by remember { mutableStateOf(surfboard.pricePerDay?.toString() ?: "") }
    var priceError by remember { mutableStateOf<String?>(null) }
    val windowInfo = LocalWindowInfo.current

    val onValidateAndConfirm: () -> Unit = {
        val parsedPrice = pricePerDay.toDoubleOrNull()
        if (parsedPrice == null || parsedPrice <= 0) {
            priceError = "Invalid price. Please enter a positive number."
        } else {
            priceError = null
            onConfirm(parsedPrice)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .then(
                        if (windowInfo.widthSize == WindowWidthSize.COMPACT) Modifier.fillMaxWidth(0.9f)
                        else Modifier.widthIn(min = 400.dp, max = 500.dp)
                    ),
                elevation = CardDefaults.cardElevation(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close dialog",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (windowInfo.widthSize == WindowWidthSize.COMPACT) {
                        PublishRentalContent(
                            surfboardName = surfboard.model,
                            pricePerDay = pricePerDay,
                            priceError = priceError,
                            onPriceChange = { pricePerDay = it },
                            onConfirm = onValidateAndConfirm,
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 24.dp, vertical = 48.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Publish ${surfboard.model} for Rental",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Set a daily price for your surfboard rental offer.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            )
                            Spacer(Modifier.height(16.dp))

                            CustomTextField(
                                value = pricePerDay,
                                onValueChange = { pricePerDay = it },
                                label = "Price per Day ($)",
                                errorMessage = priceError,
                                isError = priceError != null,
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            )
                            Spacer(Modifier.height(24.dp))

                            Button(
                                onClick = onValidateAndConfirm,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = OceanPalette.SandOrange)
                            ){
                                Text(
                                    text = "Publish",
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PublishRentalContent(
    surfboardName: String,
    pricePerDay: String,
    priceError: String?,
    onPriceChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 48.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Publish ${surfboardName} for Rental",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Set a daily price for your surfboard rental offer.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        CustomTextField(
            value = pricePerDay,
            onValueChange = onPriceChange,
            label = "Price per Day ($)" ,
            modifier = Modifier.fillMaxWidth(),
            errorMessage = priceError,
            isError = priceError != null,
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onConfirm,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = OceanPalette.SandOrange)
        ){
            Text(
                text = "Publish",
                color = Color.White,
            )
        }
    }
}
