package org.example.quiversync.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

class AndroidLocationProvider(private val context: Context): LocationProvider {
    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED

        if (!hasPermission) return null

        val androidLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: return null

        return Location(
            latitude = androidLocation.latitude,
            longitude = androidLocation.longitude
        )
    }
}