//
//  LoginScreen.swift
//  iosApp
// 
//  Created by gal levi on 03/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared
import Foundation

struct LoginScreen: View {
    @ObservedObject private(set) var viewModel = LoginViewModelWrapper()
    let onRegisterClick: () -> Void
    let onForgotPasswordClick: () -> Void
    let onNavigateToOnboarding: () -> Void
    let onBackBtn: () -> Void
    @Binding var isLoggedIn: Bool
    let onLoginSuccess: () -> Void

    var body: some View{
        VStack{
            switch onEnum(of: viewModel.uistate) {
            case .idle(let idle):
                LoginView(
                    onRegisterClick: onRegisterClick,
                    loginData: idle.data,
                    loginViewModel: viewModel.viewModel,
                    onForgotPasswordClick: onForgotPasswordClick,
                    onLoginSuccess:onLoginSuccess
                )
            case .loading:
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 300)
            case .loaded:
                Color.clear.onAppear {
                    print("User logged in successfully")
                    onLoginSuccess()
                    isLoggedIn = true
                }

            case .error(let error):
                AuthErrorView(
                    title: "Login Failed 🌧️",
                    message: error.message,
                    primaryButtonText: "Try Again",
                    onPrimaryTap: {
                        isLoggedIn = false
                        viewModel.viewModel.resetState()
                        onBackBtn()
                    },
                    secondaryButtonText: "Back to Welcome",
                    onSecondaryTap: {
                        isLoggedIn = false
                        viewModel.viewModel.resetState()
                        onBackBtn()
                    }
                )

                
            case .navigateToOnboarding:
                Color.clear.onAppear {
                    onNavigateToOnboarding()
                }
                Text("Navigate to Onboarding")
            }
        }
        .onAppear{
            viewModel.startObserving()
        }
    }
}
