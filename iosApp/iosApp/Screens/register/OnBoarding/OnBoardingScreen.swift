//
//  OnBoardingScreen.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared

struct OnBoardingScreen: View {
    
    @ObservedObject private(set) var viewModel = OnBoardingViewModelWrapper()
    
    let onCompleteClick: () -> Void // 👈 trigger navigation back to home
    @Binding var isLoggedIn: Bool
    
    var body: some View {
        VStack {
            switch onEnum(of: viewModel.uistate) {
            case .loading:
                LoadingView(colorName: "background")

            case .idle(let idle):
                OnBoardingView(
                    form: idle.data,
                    onEvent: viewModel.viewModel.onEvent
                )

            case .success:
                
                Color.clear.onAppear {
                    
                    isLoggedIn = true
                    onCompleteClick()
                }

            case .error(let error):
                AuthErrorView(
                    title: "Coudlnt complete your Profile unfortntely. Please try again later.",
                    message: error.message,
                    primaryButtonText: "Try Again",
                    onPrimaryTap: {
                        viewModel.startObserving()
                    },
                    secondaryButtonText: "Back to Welcome",
                    onSecondaryTap: {
                        isLoggedIn = false
                        onCompleteClick()
                    }
                )

            }
        }
        .onAppear {
            viewModel.startObserving()
        }
        .navigationBarBackButtonHidden(true)
    }
}
