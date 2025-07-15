package org.example.quiversync.features.quiver.add_board

import org.example.quiversync.domain.model.FinsSetup
import org.example.quiversync.domain.model.SurfboardType


data class AddBoardFormData (
    val model: String = "",
    val modelError: String? = null,
    val company: String = "",
    val companyError: String? = null,
    val boardType: SurfboardType = SurfboardType.SHORTBOARD,
    val height: String = "",
    val heightError: String? = null,
    val width: String = "",
    val widthError: String? = null,
    val volume: String = "",
    val volumeError: String? = null,
    val finSetup: FinsSetup = FinsSetup.THRUSTER,
    val currentStep: Int = 1,
    val totalSteps: Int = 2,
    val isUploadingImage: Boolean = false,
    val imageUrl: String? = null,
    val isSubmitting: Boolean = false,
    val submissionError: String? = null,
    val submissionSuccess: Boolean = false,
    val surfboardImageError: String? = null,
    val imageUploadError: String? = null,
    val uploadFromIOS: Boolean = false,
)