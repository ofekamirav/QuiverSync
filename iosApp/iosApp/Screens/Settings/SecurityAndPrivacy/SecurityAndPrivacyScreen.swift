//
//  SecurityAndPrivacyScreen.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

public struct SecurityAndPrivacyScreen: View {
    
    @ObservedObject private(set) var viewModel = SecurityAndPrivacyViewModelWrapper()
    @State private var showToast = false
    @State private var navigateToProfile = false
    
    public var body: some View {
        VStack{
            switch onEnum(of: viewModel.uiState){
            case .editing(let editing):
                SecurityAndPrivacyView(
                    form: editing.form,
                    onEvent: viewModel.viewModel.onEvent,
                    showToast: $showToast,
                    navigateToProfile: $navigateToProfile
                )

            case .error(let error):
                ErrorView(
                    title: "Settings Failed to Save 🔒",
                    message: error.message,
                    systemImageName: "lock.shield.fill",
                    buttonText: "Try Again",
                    onRetry: {
                        viewModel.startObserving()
                    }
                )

            case .loading:
                LoadingView(colorName: "foamwhite")
            case .success:
                VStack {
                    LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 200)
                }
                .onAppear {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
                        navigateToProfile = true
                    }
                }

            }
        }
        .onAppear(){
            viewModel.startObserving()
        }
        .onChange(of: viewModel.uiState){ newState in
            if newState is SecurityAndPrivacyState.Success {
                showToast = true
                navigateToProfile = true
            }
            
        }

    }
}
