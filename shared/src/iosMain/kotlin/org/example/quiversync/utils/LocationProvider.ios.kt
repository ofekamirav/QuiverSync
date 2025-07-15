package org.example.quiversync.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.*
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import platform.CoreLocation.CLLocation
import kotlinx.cinterop.useContents


@OptIn(ExperimentalForeignApi::class)
class IOSLocationProvider : LocationProvider {
    private val manager = CLLocationManager().apply {
        desiredAccuracy = kCLLocationAccuracyBest
        requestWhenInUseAuthorization()
    }

    override suspend fun getCurrentLocation(): Location? =
        suspendCancellableCoroutine { cont ->
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(
                    manager: CLLocationManager,
                    didUpdateLocations: List<*>
                ) {
                    val loc = (didUpdateLocations.lastOrNull() as? CLLocation) ?: run {
                        cont.resume(null)
                        return
                    }
                    loc.coordinate.useContents {
                        cont.resume(Location(this.latitude, this.longitude))
                    }
                    manager.stopUpdatingLocation()
                }
                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: NSError
                ) {
                    cont.resumeWithException(Throwable(didFailWithError.localizedDescription))
                    manager.stopUpdatingLocation()
                }
            }
            manager.delegate = delegate
            manager.requestLocation()
            cont.invokeOnCancellation { manager.stopUpdatingLocation() }
        }
}
