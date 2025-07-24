package org.example.quiversync.domain.model

import kotlinx.serialization.Serializable
import org.example.quiversync.data.local.Error

@Serializable
data class AuthError(
    override val message: String
):Error


@Serializable
data class SurfboardError(
    override val message: String
): Error


@Serializable
data class UserError(
    override val message: String
): Error

@Serializable
data class CloudinaryError(
    override val message: String
): Error

@Serializable
data class GeminiError(
    override val message: String
): Error

@Serializable
data class SpotsError(
    override val message: String
): Error

@Serializable
data class RentalError(
    override val message: String
): Error