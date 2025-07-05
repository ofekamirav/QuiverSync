package org.example.quiversync.domain.model

import kotlinx.serialization.Serializable
import org.example.quiversync.data.local.Error

@Serializable
data class Surfboard(
    val id: String,
    val ownerId: String,
    val model: String,
    val company: String,
    val type: SurfboardType,
    val height: String,
    val width: String,
    val volume: String,
    val finSetup: FinsSetup,
    val imageRes: String,
    val addedDate: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isRentalPublished: Boolean? = null,
    val isRentalAvailable: Boolean? = null,
    val pricePerDay: Double?= null
)

enum class SurfboardType(val serverName: String) {
    SHORTBOARD("Shortboard"),
    LONGBOARD("Longboard"),
    SOFTBOARD("Softboard"),
    FISHBOARD("Fishboard"),
    FUNBOARD("Funboard")
}
enum class FinsSetup(val serverName: String) {
    SINGLE("Single"),
    TWIN("Twin"),
    THRUSTER("Thruster"),
    QUAD("Quad"),
    FIVEFINS("Five_Fins")
}

@Serializable
data class SurfboardError(
    override val message: String
): Error