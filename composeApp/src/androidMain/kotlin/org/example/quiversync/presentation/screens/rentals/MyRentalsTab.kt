package org.example.quiversync.presentation.screens.rentals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.example.quiversync.features.rentals.my_rentals.MyRentalsState
import org.example.quiversync.features.rentals.my_rentals.MyRentalsViewModel
import org.example.quiversync.presentation.widgets.rentals_screen.RentalRequestList

@Composable
fun MyRentalsTab(
    viewModel: MyRentalsViewModel = MyRentalsViewModel()
){
    val uiState = viewModel.uiState.collectAsState().value
    when(uiState) {
        is MyRentalsState.Loading -> {

        }
        is MyRentalsState.Loaded -> {
            RentalRequestList(requests = uiState.myRentalRequests)
        }
        is MyRentalsState.Error ->{}
    }

}
