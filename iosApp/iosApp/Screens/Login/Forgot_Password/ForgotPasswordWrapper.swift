//
//  ForgotPasswordWrapper.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI
import Shared
import Foundation


extension ForgotPasswordScreen {
    
    @MainActor
    class ForgotPasswordWrapper: ObservableObject {
        
        let viewModel: ForgotPasswordViewModel
        @Published var uistate: ForgotPasswordState
        
        init(){
            self.viewModel = KoinKt.forgotPasswordViewModel()
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
    
    


