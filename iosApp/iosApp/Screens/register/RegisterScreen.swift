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
                LoadingView(colorName: "background")
                
            case .idle(let idle):
                RegisterView(
                    state: idle.data,
                    RegViewModel: viewModel.viewModel,
                    onBackClick: onBackBtn,
                    onSuccess: onSuccess
                )
            case .loaded(let loaded):
                MainTabView(isLoggedIn : $isLoggedIn)
            case .error(let error):
                ErrorView(messege: error.message)
            
            }
        }
        .onAppear {
            viewModel.startObserving()
        }
//        .onReceive(viewModel.$uistate) { newState in
//            if case .loaded(let loaded) = onEnum(of: newState), loaded.data.isWaiting {
//                onSuccess()
//            }
//        }


    }
}
