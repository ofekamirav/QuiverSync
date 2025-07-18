//
//  SecurityAndPrivacyWrapper.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared


extension SecurityAndPrivacyScreen{
    @MainActor
    class SecurityAndPrivacyViewModelWrapper: ObservableObject {
        
        let viewModel: SecurityAndPrivacyViewModel
        @Published var uiState: SecurityAndPrivacyState
        
        init(){
            self.viewModel = KoinKt.securityAndPrivacyViewModel()
            self.uiState = viewModel.uiState.value
        }
        
        func startObserving() {
            Task {
                for await state in viewModel.uiState {
                    self.uiState = state
                }
            }
        }
        
        
    }

}
