package org.example.quiversync.features.exploreRentals

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.quiversync.data.local.Result
import org.example.quiversync.domain.model.RentalOffer
import org.example.quiversync.domain.model.User
import org.example.quiversync.features.BaseViewModel
import kotlin.math.exp

class ExploreViewModel (
    private val exploreUseCases: ExploreUseCases
) : BaseViewModel(){

    private val _uiState = MutableStateFlow<ExploreState>(ExploreState.Loading)
    val uiState: StateFlow<ExploreState> get() = _uiState

    init{
        fetchBoardsForRentals()
    }

    private fun fetchBoardsForRentals() {
        scope.launch {
            _uiState.value = ExploreState.Loading

            val builtOffers = mutableListOf<RentalOffer>()

            try {
                val userResult = exploreUseCases.getUserProfileUseCase()
                var user = User(
                    uid = "",
                    name = "",
                    email = "",
                )
                when (userResult) {
                    is Result.Success<*> -> {
                        user = userResult.data as User
                    }
                    is Result.Failure<*> -> {
                        _uiState.value = ExploreState.Error("Error fetching user profile: ${userResult.error?.message}")
                        return@launch
                    }
                }

                when ( val availableBoardsResult = exploreUseCases.getAvailableBoardsUseCase(userId =user.uid)){
                    is Result.Success -> {
                        val boards = availableBoardsResult.data
                        if (boards == null) {
                            _uiState.value = ExploreState.Error("No boards available for rent at the moment.")
                            return@launch
                        }
                        if (boards.isEmpty()) {
                            _uiState.value = ExploreState.Error("No boards available for rent at the moment.")
                            return@launch
                        }

                        for (board in boards) {
                            val ownerResult = exploreUseCases.getUserByIDUseCase(board.ownerId)
                            if (ownerResult is Result.Failure) {
                                _uiState.value = ExploreState.Error("Error fetching owner details: ${ownerResult.error?.message}")
                                return@launch
                            }

                            val owner = (ownerResult as Result.Success).data ?: run {
                                _uiState.value = ExploreState.Error("Owner not found for board: ${board.id}")
                                return@launch
                            }

                            when (val offerResult = exploreUseCases.buildRentalOfferUseCase(board, owner)) {
                                is Result.Success -> offerResult.data?.let { builtOffers.add(it) }
                                is Result.Failure -> {
                                    _uiState.value = ExploreState.Error("Error building rental offer for board: ${board.id} - ${offerResult.error?.message}")
                                    return@launch
                                }
                            }
                        }
                        _uiState.value = ExploreState.Loaded(builtOffers)
                    }
                    is Result.Failure -> {
                        _uiState.value = ExploreState.Error("Error fetching rental boards: ${availableBoardsResult.error?.message}")
                        return@launch
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ExploreState.Error("Failed to fetch rental boards: ${e.message}")
            }
        }
    }

    fun refreshBoards() {
        fetchBoardsForRentals()
    }
}





//                when (val surfboardsResult= exploreUseCases.getRentalsBoardsUseCase()){
//                    is Result.Success ->{
//                        val surfboards = surfboardsResult.data
//                        if(surfboards == null){
//                            _uiState.value = ExploreState.Error("No rental boards available at the moment.")
//                            return@launch
//                        }
//                        if(surfboards.isEmpty()) {
//                            _uiState.value = ExploreState.Error("No rental boards available at the moment.")
//                            return@launch
//                        }
//                        _uiState.value = ExploreState.Loaded(surfboards)
//                    }
//                    is Result.Failure -> {
//                        _uiState.value = ExploreState.Error("Error fetching rental boards: ${surfboardsResult.error?.message}")
//                        return@launch
//                    }
//                }