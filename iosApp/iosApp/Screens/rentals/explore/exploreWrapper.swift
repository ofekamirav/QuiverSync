//
//  exploreWrapper.swift
//  iosApp
//
//  Created by gal levi on 21/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared
import Foundation


extension ExploreScreen{
    
    @MainActor
    class ExploreScreenModelWrapper: ObservableObject {
        let viewModel: ExploreViewModel
        @Published var uiState: ExploreState
        
        init() {
            self.viewModel = KoinKt.exploreViewModel()
            self.uiState = viewModel.uiState.value
            
        }
        
        func startObserving() {
            Task{
                for await state in viewModel.uiState{
                    self.uiState = state
                }
            }
        }
    }
}
