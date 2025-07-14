//
//  AddBoardWrapper.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared
import Foundation

extension AddBoardScreen {
    @MainActor
    class AddBoardViewModelWrapper: ObservableObject {
        let ViewModel: AddBoardViewModel
        @Published var uiState: AddBoardState

        init() {
            self.ViewModel = KoinKt.addBoardViewModel()
            self.uiState = ViewModel.uiState.value
        }

        func startObserving() {
            // Observe uiState
            Task {
                for await state in ViewModel.uiState {
                    self.uiState = state
                }
            }
        }
    }
}
