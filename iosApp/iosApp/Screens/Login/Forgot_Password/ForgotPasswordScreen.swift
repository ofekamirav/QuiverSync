//
//  ForgotPasswordScreen.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI
import Shared
import Foundation

struct ForgotPasswordScreen: View {
    @ObservedObject private(set) var viewModel = ForgotPasswordWrapper()
    @Binding var isLoggedIn: Bool
    var body: some View{
        VStack{
            switch onEnum(of: viewModel.uistate) {
            case .idle(let idle):
                ForgotPasswordView(formData: idle.data,
                                   onEvent: viewModel.viewModel.onEvent)
            case .loading:
                LoadingView(colorName: "background")
            case .success:
                ForgotPasswordSuccessView(
                    message: "A password reset link has been sent. Please check your inbox (and spam folder).",
                    onLoginClick: {
                        isLoggedIn = true
                    }
                )
            case .error(let error):
                ErrorView(messege: error.message)
            }
        }
        .onAppear{
            viewModel.startObserving()
            
        }
    }
}
