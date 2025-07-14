//
//  HomeWrapper.swift
//  iosApp
//
//  Created by gal levi on 14/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI
import Shared
import Foundation


extension HomeScreen {
    
    @MainActor
    class HomeViewModelWrapper: ObservableObject {
        
        let viewModel: HomeViewModel
        @Published var uistate: HomeState
        
        init(){
            self.viewModel = KoinKt.homePageViewModel()
            self.uistate = viewModel.uiState.value
        }
        
        func startObserving() {
            Task {
                for await state in viewModel.uiState {
                    self.uistate = state
                }
            }
        }
    }
}
    
    


