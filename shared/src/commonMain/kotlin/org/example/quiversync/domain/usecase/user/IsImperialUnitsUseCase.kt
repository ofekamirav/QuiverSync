package org.example.quiversync.domain.usecase.user

import org.example.quiversync.data.session.SessionManager

class IsImperialUnitsUseCase(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(): Boolean {
        return sessionManager.getUnitsPreference() == "imperial"
    } 
}