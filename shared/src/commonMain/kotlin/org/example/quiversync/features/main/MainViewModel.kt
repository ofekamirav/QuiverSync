// MainViewModel.kt

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.example.quiversync.data.session.SessionManager
import org.example.quiversync.domain.usecase.user.LogoutUseCase
import org.example.quiversync.domain.usecase.user.StartSyncsUseCase
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.utils.extensions.platformLogger

class MainViewModel(
    private val sessionManager: SessionManager,
    private val startSyncsUseCase: StartSyncsUseCase,
): BaseViewModel() {

    private val _uidState = MutableStateFlow<String?>(null)
    val uidState: StateFlow<String?> = _uidState.asStateFlow()

    private val _isInitialCheckDone = MutableStateFlow(false)
    val isInitialCheckDone: StateFlow<Boolean> = _isInitialCheckDone.asStateFlow()

    private val _isAppReady = MutableStateFlow(false)
    val isAppReady: StateFlow<Boolean> = _isAppReady.asStateFlow()

    private val _justRegistered = MutableStateFlow(false)
    val justRegistered: StateFlow<Boolean> = _justRegistered

    fun signalRegistrationComplete() {
        _justRegistered.value = true
    }

    fun consumeRegistrationSignal() {
        _justRegistered.value = false
    }

    private var currentSyncJob: Job? = null

    init {
        platformLogger("MainViewModel", "🟢 MainViewModel initialized")
        observeAuthenticationState()
    }

    private fun observeAuthenticationState() {
        scope.launch {
            try {
                sessionManager.observeUid()
                    .distinctUntilChanged()
                    .collect { uid ->
                        platformLogger("MainViewModel", "🧲 observeUid triggered collect block — current uid: $uid")

                        _uidState.value = uid

                        currentSyncJob?.cancel()
                        platformLogger("MainViewModel", "🔴 Cancelled previous sync job")

                        if (uid != null) {
                            platformLogger("MainViewModel", "✅ UID set: $uid — starting syncs")

                            _isInitialCheckDone.value = false
                            _isAppReady.value = false

                            currentSyncJob = launch {
                                try {
                                    platformLogger("MainViewModel", "🚀 startSyncsUseCase launched")
                                    startSyncsUseCase()

                                    delay(500)

                                    platformLogger("MainViewModel", "✅ Sync completed successfully")

                                    _isInitialCheckDone.value = true
                                    _isAppReady.value = true

                                } catch (e: Exception) {
                                    platformLogger("MainViewModel", "❌ Sync failed: ${e.message}")
                                    _isInitialCheckDone.value = true
                                    _isAppReady.value = false
                                }
                            }
                        } else {
                            platformLogger("MainViewModel", "⚠ UID is null — syncs stopped")
                            _isInitialCheckDone.value = true
                            _isAppReady.value = false
                        }
                    }
            } catch (e: Exception) {
                platformLogger("MainViewModel", "❌ Error in observeAuthenticationState: ${e.message}")
                _isInitialCheckDone.value = true
                _isAppReady.value = false
            }
        }
    }

    fun retrySync() {
        val currentUid = _uidState.value
        if (currentUid != null) {
            platformLogger("MainViewModel", "🔄 Retrying sync for UID: $currentUid")
            _isInitialCheckDone.value = false
            _isAppReady.value = false

            currentSyncJob?.cancel()
            currentSyncJob = scope.launch {
                try {
                    startSyncsUseCase()
                    delay(500)
                    _isInitialCheckDone.value = true
                    _isAppReady.value = true
                } catch (e: Exception) {
                    platformLogger("MainViewModel", "❌ Retry sync failed: ${e.message}")
                    _isInitialCheckDone.value = true
                    _isAppReady.value = false
                }
            }
        }
    }

}