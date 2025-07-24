package org.example.quiversync.features.rentals.explore

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.OwnerLocal
import org.example.quiversync.domain.model.User
import org.example.quiversync.domain.usecase.user.GetOwnerByIdUseCase
import org.example.quiversync.features.BaseViewModel
import org.example.quiversync.features.rentals.RentalsUseCases
import org.example.quiversync.utils.extensions.platformLogger
import org.example.quiversync.utils.extensions.toBoardForRent
import kotlin.also

class ExploreViewModel(
    private val rentalsUseCases: RentalsUseCases,
    private val getOwnerByIdUseCase: GetOwnerByIdUseCase
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<ExploreState>(ExploreState.Loading)
    val uiState: StateFlow<ExploreState> get() = _uiState

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _boardsUiState = MutableStateFlow<List<BoardForDisplay>>(emptyList())
    private var lastId: String? = null
    private val pageSize = 10

    init {
        observeLocalBoards()
    }

    private fun observeLocalBoards() {
        scope.launch {
            val initial = rentalsUseCases.observeExploreBoards().firstOrNull()
            if (initial.isNullOrEmpty()) loadNextPage()

            rentalsUseCases.observeExploreBoards()
                .onStart { _uiState.value = ExploreState.Loading }
                .catch { e ->
                    platformLogger("ExploreVM", "DB flow error: ${e.message}")
                    _uiState.value = ExploreState.Error("Database error")
                }
                .collectLatest { surfboards ->
                    platformLogger("ExploreVM", "Collecting ${surfboards.size} surfboards from DB")

                    val updated = surfboards.map { surf ->
                        val existing = _boardsUiState.value.find { it.id == surf.id }
                        existing ?: BoardForDisplay(board = null, id = surf.id)
                    }
                    _boardsUiState.value = updated

                    surfboards.forEach { surf ->
                        launch {
                            val owner = fetchOwner(surf.ownerId)
                            if (owner != null) {
                                val board = surf.toBoardForRent(owner)
                                _boardsUiState.update { current ->
                                    current.map {
                                        if (it.id == surf.id) it.copy(board = board) else it
                                    }
                                }
                                platformLogger("ExploreVM", "Loaded board ${surf.id} with owner ${owner.id}")
                                _uiState.value = ExploreState.Loaded(_boardsUiState.value)
                            }
                        }
                    }

                    _uiState.value = ExploreState.Loaded(_boardsUiState.value)
                }
        }
    }
    private suspend fun fetchOwner(ownerId: String): OwnerLocal? {
        return when (val res = getOwnerByIdUseCase(ownerId).firstOrNull()) {
            is Result.Success -> {
                platformLogger("ExploreVM", "Fetched owner $ownerId from local/remote")
                res.data
            }
            is Result.Failure -> {
                platformLogger("ExploreVM", "Failed to fetch owner $ownerId: ${res.error?.message}")
                null
            }
            else -> null
        }
    }

    fun loadNextPage() {
        scope.launch {
            if (_isLoadingMore.value) return@launch
            _isLoadingMore.value = true

            platformLogger("ExploreVM", "Fetching next page after $lastId")

            when (val res = rentalsUseCases.fetchExplorePage(pageSize, lastId)) {
                is Result.Success -> {
                    val boards = res.data ?: emptyList()
                    lastId = boards.lastOrNull()?.id
                    val newItems = boards.map { surf ->
                        BoardForDisplay(board = null, id = surf.id)
                    }
                    _boardsUiState.update { current ->
                        (current + newItems).distinctBy { it.id }
                    }
                    platformLogger("ExploreVM", "Fetched ${boards.size} new boards")
                    _uiState.value = ExploreState.Loaded(_boardsUiState.value)
                }
                is Result.Failure -> {
                    platformLogger("ExploreVM", "Page fetch error: ${res.error?.message}")
                    _uiState.value = ExploreState.Error("Load page failed")
                }
            }

            _isLoadingMore.value = false
        }
    }


    fun startSync() = rentalsUseCases.startRemoteSync()
    fun stopSync() = rentalsUseCases.stopRemoteSync()

}
