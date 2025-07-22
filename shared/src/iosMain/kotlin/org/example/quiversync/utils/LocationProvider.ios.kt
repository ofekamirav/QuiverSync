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
        desiredAccuracy = kCLLocationAccuracyHundredMeters
        requestWhenInUseAuthorization()
    }

//     👇 Store the delegate so it persists
    private var locationDelegate: CLLocationManagerDelegateProtocol? = null

    override suspend fun getCurrentLocation(): Location? =
        suspendCancellableCoroutine { cont ->
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(
                    manager: CLLocationManager,
                    didUpdateLocations: List<*>
                ) {
                    val loc = (didUpdateLocations.lastOrNull() as? CLLocation)
                    if (loc == null) {
                        if (cont.isActive) cont.resume(null)
                        manager.stopUpdatingLocation()
                        return
                    }

                    if (cont.isActive) {
                        loc.coordinate.useContents {
                            cont.resume(Location(latitude, longitude))
                        }
                        manager.stopUpdatingLocation()
                    }
                }

                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: NSError
                ) {
                    if (cont.isActive) {
                        cont.resumeWithException(Throwable(didFailWithError.localizedDescription))
                        manager.stopUpdatingLocation()
                    }
                }
            }

            locationDelegate = delegate // 👈 persist the delegate

            manager.delegate = delegate
            manager.startUpdatingLocation()

            cont.invokeOnCancellation {
                manager.stopUpdatingLocation()
                locationDelegate = null // ✅ clean up
            }
        }
}
