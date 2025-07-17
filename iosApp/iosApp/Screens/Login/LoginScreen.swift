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
    @Binding var isLoggedIn: Bool
    var body: some View{
        VStack{
            switch onEnum(of: viewModel.uistate) {
            case .idle(let idle):
                LoginView(
                    onRegisterClick: onRegisterClick,
                    loginData: idle.data,
                    loginViewModel: viewModel.viewModel,
                    onForgotPasswordClick: onForgotPasswordClick,
                )
            case .loading:
                LoadingView(colorName: "background")
            case .loaded:
                Color.clear.onAppear {isLoggedIn = true}
            case .error(let error):
                ErrorView(messege: error.message)
                
            case .navigateToOnboarding:
//                Color.clear.onAppear {
//                    onNavigateToOnboarding()
//                }
                Text("Navigate to Onboarding")
            }
        }
        .onAppear{
            viewModel.startObserving()
        }
    }
}
