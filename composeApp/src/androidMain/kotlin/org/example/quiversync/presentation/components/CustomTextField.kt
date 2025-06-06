package org.example.quiversync.presentation.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.input.ImeAction
import org.example.quiversync.presentation.theme.OceanPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    leadingIcon: ImageVector? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(60.dp),
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val colors = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (value.isNotEmpty()) OceanPalette.SurfBlue else Color.Gray
                )
            }
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = if (value.isNotEmpty()) OceanPalette.SurfBlue else Color.Gray
                    )
                }
            } else {
                trailingIcon?.invoke()
            }
        },
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = OceanPalette.DarkSky,
            cursorColor = colors.primary,
            focusedLabelColor = colors.primary,
            unfocusedLabelColor = Color.Gray,
            focusedContainerColor = colors.surfaceVariant,
        ),
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = {
                onImeAction()
                focusManager.clearFocus()
            }
        ),
        readOnly = readOnly,
        isError = isError,
        supportingText = supportingText
    )
}
