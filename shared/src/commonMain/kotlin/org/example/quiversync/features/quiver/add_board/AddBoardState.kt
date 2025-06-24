package org.example.quiversync.features.quiver.add_board

import org.example.quiversync.domain.model.SurfboardType


data class AddBoardState (
    val model: String = "",
    val company: String = "",
    val boardType: SurfboardType = SurfboardType.SHORTBOARD,
    val height: String = "",
    val width: String = "",
    val volume: String = "",
    val currentStep: Int = 1,
    val totalSteps: Int = 2,
    val isSubmitting: Boolean = false,
    val submissionError: String? = null,
    val submissionSuccess: Boolean = false
)