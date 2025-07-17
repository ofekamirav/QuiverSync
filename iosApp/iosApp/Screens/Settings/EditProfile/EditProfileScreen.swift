//
//  EditProfileScreen.swift
//  iosApp
//
//  Created by gal levi on 16/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

public struct EditProfileScreen: View {
    
    @ObservedObject private(set) var viewModel = EditProfileViewModelWrapper()
    @Binding var isLoggedIn: Bool

    
    @State private var showToast = false
    @State private var navigateToProfile = false

    
    
    public var body: some View {
        VStack{
            switch onEnum(of: viewModel.uiState){
            case .editing(let editing):
                EditProfileFormView(form: editing.form, onEvent: viewModel.viewModel.onEvent , showToast: $showToast, navigateToProfile: $navigateToProfile)
                    
                    
            case .error(let error):
                ErrorView(messege: error.message)
            case .loading:
                LoadingView(colorName: "foamwhite")
            case .success:
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 200)
            }
        }
        .onAppear(){
            viewModel.startObserving()
        }
        .onChange(of: viewModel.uiState){ newState in
            if newState is EditUserState.Success {
                showToast = true
                navigateToProfile = true
            }
            
        }

    }
}
