package org.example.quiversync.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.*
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissionsDirectly(
    onAllPermissionsGranted: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val readStoragePermissionState = rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    val fineLocationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    val allPermissionsGranted = cameraPermissionState.status.isGranted &&
            readStoragePermissionState.status.isGranted &&
            fineLocationPermissionState.status.isGranted

    LaunchedEffect(key1 = Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        } else if (!readStoragePermissionState.status.isGranted) {
            readStoragePermissionState.launchPermissionRequest()
        } else if (!fineLocationPermissionState.status.isGranted) {
            fineLocationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(key1 = allPermissionsGranted) {
        if (allPermissionsGranted) {
            onAllPermissionsGranted()
        }
    }
}
