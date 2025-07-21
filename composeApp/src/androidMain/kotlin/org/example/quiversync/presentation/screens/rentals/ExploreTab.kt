package org.example.quiversync.presentation.screens.rentals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.example.quiversync.features.rentals.explore.ExploreState_old
import org.example.quiversync.features.rentals.explore.ExploreViewModel_old
import org.example.quiversync.presentation.screens.skeletons.ExploreTabSkeleton
import org.example.quiversync.presentation.widgets.rentals_screen.RentalBoardList

@Composable
fun ExploreTab(
    viewModel: ExploreViewModel_old = ExploreViewModel_old()
){
    val uiState = viewModel.uiState.collectAsState().value
    when(uiState) {
        is ExploreState_old.Loading -> {
            ExploreTabSkeleton()
        }

        is ExploreState_old.Loaded -> {
            RentalBoardList(boards = uiState.communityBoards)
        }

        is ExploreState_old.Error ->{}
    }

}