package org.example.quiversync.presentation.screens.rentals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.example.quiversync.features.rentals.my_offers.MyOffersState
import org.example.quiversync.features.rentals.my_offers.MyOffersViewModel
import org.example.quiversync.presentation.widgets.rentals_screen.MyOffersRequestsList

@Composable
fun MyOffersTab(
    viewModel: MyOffersViewModel = MyOffersViewModel()
){
    val uiState = viewModel.uiState.collectAsState().value
    when(uiState) {
        is MyOffersState.Loading -> {
        }
        is MyOffersState.Loaded -> {
            MyOffersRequestsList(
                requests = uiState.myOfferRequests
            )
        }
        is MyOffersState.Error ->{}
    }

}
