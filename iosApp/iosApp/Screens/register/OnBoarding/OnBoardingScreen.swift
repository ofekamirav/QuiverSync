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
                    onCompleteClick() // 👈 navigate to home screen
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
