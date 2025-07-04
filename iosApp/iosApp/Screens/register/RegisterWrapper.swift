//
//  RegisterWrapper.swift
//  iosApp
//
//  Created by gal levi on 30/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import Shared

extension RegisterScreen {
    
    
    
    @MainActor
    class RegisterViewModelWrapper: ObservableObject {
        
        let viewModel: RegisterViewModel
        @Published var uistate: RegisterState
        
        
        init(){
            self.viewModel = KoinKt.registerViewModel()
            self.uistate = viewModel.registerState.value
        }
        
        func startObserving() {
            Task{
                for await state in viewModel.registerState {
                    self.uistate = state
                }
            }
        }
         



    }
}
