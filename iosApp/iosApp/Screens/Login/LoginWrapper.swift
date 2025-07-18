//
//  LoginWrapper.swift
//  iosApp
//
//  Created by gal levi on 03/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared
import Foundation


extension LoginScreen {
    
    @MainActor
    class LoginViewModelWrapper: ObservableObject {
        
        let viewModel: LoginViewModel
        @Published var uistate: LoginState
        
        init(){
            self.viewModel = KoinKt.loginViewModel()
            self.uistate = viewModel.loginState.value
        }
        
        func startObserving() {
            Task {
                for await state in viewModel.loginState {
                    self.uistate = state
                }
            }
        }
        
        
    }
}
    
    


