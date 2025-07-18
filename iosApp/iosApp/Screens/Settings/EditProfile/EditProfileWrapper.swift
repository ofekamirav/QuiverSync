//
//  EditProfileWrapper.swift
//  iosApp
//
//  Created by gal levi on 16/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared


extension EditProfileScreen {
    @MainActor
    class EditProfileViewModelWrapper: ObservableObject {
        
        let viewModel: EditProfileDetailsViewModel
        @Published var uiState: EditUserState
        @Published var isLoading: Bool = false
        
        init() {
            self.viewModel = KoinKt.editProfileDetailsViewModel()
            self.uiState = viewModel.uiState.value
        }
        
        func startObserving() {
            Task {
                // Observe UI state
                for await state in viewModel.uiState {
                    self.uiState = state
                }
            }
            
            Task {
                // Observe saveLoading
                for await isLoadingValue in viewModel.saveLoading {
                    self.isLoading = isLoadingValue.boolValue
                }
            }
        }
    }
}
