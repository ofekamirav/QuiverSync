//
//  RegisterScreen.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct RegisterScreen: View {
    
    @ObservedObject private(set) var viewModel = RegisterViewModelWrapper()
    
    let onBackBtn: () -> Void
    let onSuccess: () -> Void
    @Binding var isLoggedIn: Bool
    let onLoginSuccess : () -> Void

    
    var body: some View {
        VStack {
            switch onEnum(of: viewModel.uistate) {
            case .loading:
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 300)
            case .idle(let idle):
                RegisterView(
                    state: idle.data,
                    RegViewModel: viewModel.viewModel,
                    onBackClick: onBackBtn
                )
            case .loaded:
                Color.clear.onAppear {
                    Task{
                        viewModel.viewModel.resetState()
                    }
                    onSuccess()
                }
            case .error(let error):
                AuthErrorView(
                    title: "Registration Wipeout 🌊",
                    message: error.message,
                    primaryButtonText: "Try Again",
                    onPrimaryTap: {
                        viewModel.resetState()
                        viewModel.startObserving()
                    },
                    secondaryButtonText: "Back to Login",
                    onSecondaryTap: {
                        viewModel.resetState()
                        onBackBtn()
                    }
                )


            
            }
        }
        .onAppear {
            viewModel.startObserving()
        }


    }
}
