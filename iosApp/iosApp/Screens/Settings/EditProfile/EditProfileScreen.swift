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

    let onSucsess : () -> Void
    @State private var showToast = false
    @State private var navigateToProfile = false

    
    
    public var body: some View {
        VStack{
            switch onEnum(of: viewModel.uiState){
            case .editing(let editing):
                EditProfileFormView(
                    form: editing.form,
                    onEvent: viewModel.viewModel.onEvent,
                    showToast: $showToast,
                    navigateToProfile: $navigateToProfile,
                    loading: viewModel.isLoading
                )
                    
                    
            case .error(let error):
                ErrorView(
                    title: "Couldn’t Update Your Profile ",
                    message: error.message,
                    systemImageName: "person.crop.circle.badge.exclam",
                    buttonText: "Try Again",
                    onRetry: {
                        viewModel.startObserving()
                    }
                )

            case .loading:
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 300)
            case .success:
                VStack {
                    Spacer()

                    SuccessMessageView(
                        title: "Profile Updated!",
                        subtitle: "Your changes have been saved successfully.",
                        systemIconName: "person.crop.circle.badge.checkmark",
                        iconColor: .green,
                        backgroundColor: .green,
                        onDismiss: {
                            onSucsess()
                        }
                    )
                    .padding(.top, 80)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                    .animation(.spring(), value: UUID())

                    Spacer()
                }
                .onAppear {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        onSucsess()
                    }
                }

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
