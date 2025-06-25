// iosMain
package org.example.quiversync.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.*
import platform.darwin.NSObject
import platform.CoreLocation.CLLocationCoordinate2D

class IOSLocationProvider : NSObject(), LocationProvider {

    private val locationManager = CLLocationManager().apply {
        desiredAccuracy = kCLLocationAccuracyBest
        distanceFilter = kCLDistanceFilterNone
        requestWhenInUseAuthorization()
        startUpdatingLocation()
    }
    @OptIn(ExperimentalForeignApi::class)
    override fun getCurrentLocation(): Location? {
        //later
        return Location(27.2536, 34.6526)
    }


}
