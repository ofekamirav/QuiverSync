package org.example.quiversync.features.main

import kotlinx.coroutines.launch
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.usecase.user.StartSyncsUseCase
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.utils.extensions.platformLogger

class MainViewModel(
    private val sessionManager: SessionManager,
    private val startSyncsUseCase: StartSyncsUseCase
): BaseViewModel() {

    init {
        scope.launch {
            checkSessionAndStartCoreSyncs()
        }
    }

    private suspend fun checkSessionAndStartCoreSyncs() {
        if (sessionManager.getUid() != null) {
            platformLogger("MainViewModel", "Active session found. Initiating all core syncs...")
            startSyncsUseCase()
        } else {
            platformLogger("MainViewModel", "No active session found. Waiting for user to log in.")
        }
    }
}