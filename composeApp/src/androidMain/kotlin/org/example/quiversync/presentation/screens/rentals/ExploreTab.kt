package org.example.quiversync.presentation.screens.rentals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.example.quiversync.features.rentals.explore.ExploreState
import org.example.quiversync.features.rentals.explore.ExploreViewModel
import org.example.quiversync.presentation.screens.skeletons.ExploreTabSkeleton
import org.example.quiversync.presentation.widgets.rentals_screen.RentalBoardList

@Composable
fun ExploreTab(
    viewModel: ExploreViewModel = ExploreViewModel()
){
    val uiState = viewModel.uiState.collectAsState().value
    when(uiState) {
        is ExploreState.Loading -> {
            ExploreTabSkeleton()
        }

        is ExploreState.Loaded -> {
            RentalBoardList(boards = uiState.communityBoards)
        }

        is ExploreState.Error ->{}
    }

}