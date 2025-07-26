// MainViewModel.kt

import kotlinx.coroutines.Job
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

    private var currentSyncJob: Job? = null

    init {
        observeAuthenticationState()
    }

    private fun observeAuthenticationState() {
        scope.launch {
            sessionManager.observeUid()
                .distinctUntilChanged()
                .collect { uid ->
                    currentSyncJob?.cancel()

                    if (uid != null) {
                        platformLogger("MainViewModel", "UID observed: $uid. Starting core syncs...")
                        currentSyncJob = launch { startSyncsUseCase() }
                    } else {
                        platformLogger("MainViewModel", "UID is null. Ensuring core syncs are stopped.")
                    }
                }
        }
    }
}