//
//  FavSpotWrapper.swift
//  iosApp
//
//  Created by gal levi on 08/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI
import Shared
import Foundation


extension FavSpotsScreen {
    @MainActor
    class FavSpotsViewModelWrapper : ObservableObject{
        let viewModel: FavSpotsViewModel
        @Published var uiState: FavSpotsState
        
        init(){
            self.viewModel = KoinKt.favSpotsViewModel()
            self.uiState = viewModel.uiState.value
        }
        
        func startObserving(){
            Task{
                for await state in viewModel.uiState{
                    self.uiState = state
                }
            }
        }
    }
}
