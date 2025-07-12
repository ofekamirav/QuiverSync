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
    @Binding var isLoggedIn: Bool
    var body: some View{
        VStack{
            switch onEnum(of: viewModel.uistate) {
            case .idle(let idle):
                LoginView(
                    onRegisterClick: onRegisterClick,
                    loginData: idle.data,
                    loginViewModel: viewModel.viewModel,
                )
            case .loading:
                LoadingView(colorName: "background")
            case .loaded:
                MainTabView(isLoggedIn: $isLoggedIn)
            case .error(let error):
                ErrorView(messege: error.message)
            }
        }
        .onAppear{
            viewModel.startObserving()
        }
    }
}
