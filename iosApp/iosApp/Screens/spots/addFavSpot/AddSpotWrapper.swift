//
//  AddSpotWrapper.swift
//  iosApp
//
//  Created by gal levi on 09/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared
import Foundation


extension AddSpotScreen {
    @MainActor
    class AddSpotViewModelWrapper : ObservableObject{
        let viewModel: AddFavSpotViewModel
        @Published var uiState: AddFavSpotState
        
        init(){
            self.viewModel = KoinKt.addFavSpotViewModel()
            self.uiState =  viewModel.addFavSpotState.value
        }
        
        func startObserving(){
            Task{
                for await state in viewModel.addFavSpotState{
                    self.uiState = state
                }
            }
        }
    }
}
