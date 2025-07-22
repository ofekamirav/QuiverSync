package org.example.quiversync.features.rentals.explore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.usecase.user.GetUserByIdUseCase
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.rentals.RentalsUseCases
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.utils.extensions.toBoardForRent

class ExploreViewModel(
    private val rentalsUseCases: RentalsUseCases,
    private val getUserById: GetUserByIdUseCase,
    ) : BaseViewModel(){
    private val _uiState = MutableStateFlow<ExploreState>(ExploreState.Loading)
    val uiState: StateFlow<ExploreState> get() = _uiState

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val userCache = mutableMapOf<String, User>()
    private var lastId: String? = null
    private val pageSize = 10


    init {
        loadNextPage()
        observeLocal()
    }

    private fun observeLocal() {
        scope.launch {
            rentalsUseCases.observeExploreBoards()
                .onStart { _uiState.value = ExploreState.Loading }
                .catch { e ->
                    platformLogger("ExploreVM", "DB flow error: ${e.message}")
                    _uiState.value = ExploreState.Error("Database error")
                }
                .collectLatest { surfboards ->
                    val boardsForRent = surfboards.map { surf ->
                        val owner = userCache[surf.ownerId]
                            ?: fetchAndCacheUser(surf.ownerId)
                        surf.toBoardForRent(owner)
                    }
                    _uiState.value = ExploreState.Loaded(boardsForRent)
                }
        }
    }


    private suspend fun fetchAndCacheUser(ownerId: String): User? {
        return when (val res = getUserById(ownerId).firstOrNull()) {
            is Result.Success -> {
                val user = res.data
                if (user != null) {
                    userCache[ownerId] = user
                }
                user
            }
            is Result.Failure -> {
                platformLogger("ExploreVM", "Failed fetch user $ownerId: ${res.error?.message}")
                null
            }

            else -> {
                platformLogger("ExploreVM", "Unknown result type for user $ownerId")
                null
            }
        }
    }


    fun loadNextPage() {
        scope.launch {
            if (_isLoadingMore.value) return@launch

            _isLoadingMore.value = true
            when (val res = rentalsUseCases.fetchExplorePage(pageSize, lastId)) {
                is Result.Success -> {
                    lastId = res.data?.lastOrNull()?.id
                }
                is Result.Failure -> {
                    platformLogger("ExploreVM", "Page fetch error: ${res.error?.message}")
                     _uiState.value = ExploreState.Error("Load page failed")
                }
            }
            _isLoadingMore.value = false
        }
    }

}
