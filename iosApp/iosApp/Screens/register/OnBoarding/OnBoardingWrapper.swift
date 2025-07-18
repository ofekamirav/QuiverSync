//
//  OnBoardingWrapper.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import Foundation
import SwiftUI
import Shared

extension OnBoardingScreen {
    
    
    
    @MainActor
    class OnBoardingViewModelWrapper: ObservableObject {
        
        let viewModel: OnboardingViewModel
        @Published var uistate: OnboardingState
        
        
        init(){
            self.viewModel = KoinKt.onboardingViewModel()
            self.uistate = viewModel.onboardingState.value
        }
        
        func startObserving() {
            Task{
                for await state in viewModel.onboardingState {
                    self.uistate = state
                }
            }
        }
         



    }
}
