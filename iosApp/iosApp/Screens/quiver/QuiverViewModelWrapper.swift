//
//  QuiverViewModelWrapper.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared
import Foundation

extension QuiverScreen {
    @MainActor
    class QuiverViewModelWrapper: ObservableObject {
        let ViewModel: QuiverViewModel
        @Published var uiState: QuiverState
        @Published var boardToPublishLatest: Surfboard? = nil

        init() {
            self.ViewModel = KoinKt.quiverViewModel()
            self.uiState = ViewModel.uiState.value
        }

        func startObserving() {
            // Observe uiState
            Task {
                for await state in ViewModel.uiState {
                    self.uiState = state
                }
            }

            // Observe boardToPublish (StateFlow<Surfboard?>)
            Task {
                for await board in ViewModel.boardToPublish {
                    self.boardToPublishLatest = board
                }
            }
        }
    }
}
