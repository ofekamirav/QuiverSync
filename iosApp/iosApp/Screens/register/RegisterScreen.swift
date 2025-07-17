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
    
    var body: some View {
        VStack {
            switch onEnum(of: viewModel.uistate) {
            case .loading:
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 200)
            case .idle(let idle):
                RegisterView(
                    state: idle.data,
                    RegViewModel: viewModel.viewModel,
                    onBackClick: onBackBtn
                )
            case .loaded:
                Color.clear.onAppear {
                        onSuccess()
                    }
            case .error(let error):
                ErrorView(messege: error.message)
            
            }
        }
        .onAppear {
            viewModel.startObserving()
        }


    }
}
