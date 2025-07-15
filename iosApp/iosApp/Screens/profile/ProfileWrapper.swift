//
//  ProfileWrapper.swift
//  iosApp
//
//  Created by gal levi on 15/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared
import Foundation

//using SKIE for async req , not using corutines
//Get the state from the Kotlin.
extension ProfileScreen{
    
    @MainActor
    class ProfileScreenModelWrapper: ObservableObject {
        let viewModel: UserViewModel
        @Published var uiState: UserState
        
        init() {
            self.viewModel = KoinKt.userViewModel()
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
