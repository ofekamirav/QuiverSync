package org.example.quiversync.utils.extensions

import org.example.quiversync.SurfboardEntity
import org.example.quiversync.data.remote.dto.SurfboardDto
import org.example.quiversync.domain.model.FinsSetup
import org.example.quiversync.domain.model.SurfLevel
import org.example.quiversync.domain.model.Surfboard
import org.example.quiversync.domain.model.SurfboardType

fun SurfboardEntity.toDomain(): Surfboard {
    return Surfboard(
        id = id,
        ownerId = ownerId,
        model = model,
        company = company,
        type = type.toSurfboardType(),
        height = height,
        width = width,
        volume = volume,
        finSetup = finSetup.toFinsSetup(),
        imageRes = imageRes,
        addedDate = addedDate,
        latitude = latitude,
        longitude = longitude,
        isRentalPublished = isRentalPublished?.toBoolean(),
        isRentalAvailable = isRentalAvailable?.toBoolean(),
        pricePerDay = pricePerDay,
    )
}

fun Surfboard.toDto(): SurfboardDto {
    return SurfboardDto(
        ownerId = this.ownerId,
        model = this.model,
        company = this.company,
        type = this.type.toEntity(),
        height = this.height,
        width = this.width,
        volume = this.volume,
        finSetup = this.finSetup.toEntity(),
        imageRes = this.imageRes,
        addedDate = this.addedDate,
        latitude = this.latitude,
        longitude = this.longitude,
        isRentalPublished = this.isRentalPublished,
        isRentalAvailable = this.isRentalAvailable,
        pricePerDay = this.pricePerDay
    )
}
fun SurfboardDto.toDomain(id: String): Surfboard {
    return Surfboard(
        id = id,
        ownerId = this.ownerId.toString(),
        model = this.model.toString(),
        company = this.company.toString(),
        type = this.type?.toSurfboardType() ?: SurfboardType.SHORTBOARD,
        height = this.height.toString(),
        width = this.width.toString(),
        volume = this.volume.toString(),
        finSetup = finSetup?.toFinsSetup() ?: FinsSetup.THRUSTER,
        imageRes = this.imageRes.toString(),
        addedDate = this.addedDate.toString(),
        latitude = this.latitude,
        longitude = this.longitude,
        isRentalPublished = this.isRentalPublished,
        isRentalAvailable = this.isRentalAvailable,
        pricePerDay = this.pricePerDay
    )
}

fun SurfboardDto.toEntity(id: String): SurfboardEntity {
    return SurfboardEntity(
        id = id ,
        ownerId = this.ownerId.toString(),
        model = this.model.toString(),
        company = this.company.toString(),
        type = this.type ?: SurfboardType.SHORTBOARD.toEntity(),
        height = this.height.toString(),
        width = this.width.toString(),
        volume = this.volume.toString(),
        finSetup = this.finSetup ?: FinsSetup.THRUSTER.toEntity(),
        imageRes = this.imageRes.toString(),
        addedDate = this.addedDate.toString(),
        latitude = this.latitude,
        longitude = this.longitude,
        isRentalPublished = this.isRentalPublished?.toLong(),
        isRentalAvailable = this.isRentalAvailable?.toLong(),
        pricePerDay = this.pricePerDay
    )
}

fun Surfboard.toEntity(): SurfboardEntity {
    return SurfboardEntity(
        id = id,
        ownerId = ownerId,
        model = model,
        company = company,
        type = type.toEntity(),
        height = height,
        width = width,
        volume = volume,
        finSetup = finSetup.toEntity(),
        imageRes = imageRes,
        addedDate = addedDate,
        latitude = latitude,
        longitude = longitude,
        isRentalPublished = isRentalPublished?.toLong(),
        isRentalAvailable = isRentalAvailable?.toLong(),
        pricePerDay = pricePerDay
    )
}

fun FinsSetup.toEntity(): String {
    return when (this) {
        FinsSetup.FIVEFINS -> "Five Fins"
        FinsSetup.THRUSTER -> "Thruster"
        FinsSetup.QUAD -> "Quad"
        FinsSetup.TWIN -> "Twin"
        FinsSetup.SINGLE -> "Single"
    }
}

fun String.toFinsSetup(): FinsSetup {
    return when (this) {
        "Five Fins" -> FinsSetup.FIVEFINS
        "Thruster" -> FinsSetup.THRUSTER
        "Quad" -> FinsSetup.QUAD
        "Twin" -> FinsSetup.TWIN
        "Single" -> FinsSetup.SINGLE
        else -> throw IllegalArgumentException("Unknown fins setup: $this")
    }
}

fun SurfboardType.toEntity(): String {
    return when (this) {
        SurfboardType.SHORTBOARD -> "Shortboard"
        SurfboardType.LONGBOARD -> "Longboard"
        SurfboardType.FISHBOARD -> "Fishboard"
        SurfboardType.FUNBOARD -> "Funboard"
        SurfboardType.SOFTBOARD -> "Softboard"
    }
}
fun String.toSurfboardType(): SurfboardType {
    return when (this) {
        "Shortboard" -> SurfboardType.SHORTBOARD
        "Longboard" -> SurfboardType.LONGBOARD
        "Fishboard" -> SurfboardType.FISHBOARD
        "Funboard" -> SurfboardType.FUNBOARD
        "Softboard" -> SurfboardType.SOFTBOARD
        else -> throw IllegalArgumentException("Unknown surfboard type: $this")
    }
}

fun String.toSurfLevel(): SurfLevel {
    return when (this) {
        "Beginner" -> SurfLevel.BEGINNER
        "Intermediate" -> SurfLevel.INTERMEDIATE
        "Pro" -> SurfLevel.PRO
        else -> throw IllegalArgumentException("Unknown surf level: $this")
    }
}

fun Boolean.toLong(): Long? {
    return if (this) 1L else 0L
}

fun Long.toBoolean(): Boolean {
    return this == 1L
}

