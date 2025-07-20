//
//  LoginScreen.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct LoginView: View {
    @Environment(\.colorScheme) var colorScheme

    let onRegisterClick: () -> Void
    
    let loginData: LoginData
    
    let loginViewModel: LoginViewModel
    
    let onForgotPasswordClick: () -> Void
    
    let onLoginSuccess: () -> Void
    
    @State private var email: String = ""
    @State private var password: String = ""



    var logoTint: Color {
            colorScheme == .dark ? AppColors.skyBlue : AppColors.deepBlue
    }

    var cardColor: Color {
        colorScheme == .dark ? AppColors.darkCard : .white
    }

    var textColor: Color {
        colorScheme == .dark ? AppColors.darkText : AppColors.textDark
    }

    var body: some View {
        ZStack {
            LinearGradient(
                colors: colorScheme == .dark ? AppColors.loginGradientDark : AppColors.loginGradientLight,
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack {
                Spacer()

                VStack(spacing: 16) {
                    Image("logo")
                        .resizable()
                        .frame(width: 68, height: 68)
                        .foregroundColor(logoTint)
                        .clipShape(Circle())

                    Text("QuiverSync")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(logoTint)

                    Text("Welcome Back")
                        .font(.headline)
                        .foregroundColor(textColor)

                    CustomInputField(
                        label: "Email",
                        text: $email,
                        systemImage: "envelope"
                    )
                    .onChange(of: email){ newValue in
                        loginViewModel.onEvent(event:
                                                LoginEventEmailChanged(value: newValue))
                    }
                    if let emailError = loginData.emailError {
                        Text(emailError)
                            .foregroundColor(.red)
                            .font(.caption)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 4)
                    }

                    CustomInputField(
                        label: "Password",
                        text: $password,
                        systemImage: "lock",
                        isSecure: true
                    )
                    .onChange(of: password) { newValue in


                        loginViewModel.onEvent(event:
                                                LoginEventPasswordChanged(value: newValue))

                    }
                    
                    if let passwordError = loginData.passwordError {
                        Text(passwordError)
                            .foregroundColor(.red)
                            .font(.caption)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 4)
                    }

                    HStack {
                        Spacer()
                        Button(action: onForgotPasswordClick) {
                            Text("Forgot Password?")
                                .font(.caption)
                                .foregroundColor(logoTint)
                        }
                        .font(.caption)
                        .foregroundColor(.gray)
                    }

                    GradientButton(text: "Sign In"){
                        loginViewModel.onEvent(event:
                                                LoginEventSignInClicked())

                                                
                    }

                    DividerWithText(text: "Or continue with")

                    HStack(spacing: 12) {
                        SocialLoginButton(text: "Google", imageName: "g.circle") {
                            print("Google login tapped")
                        }

                        SocialLoginButton(text: "Apple", imageName: "apple.logo") {
                            SignInWithAppleCoordinator(viewModel: loginViewModel).startSignIn()
                        }
                    }

                    Button(action: onRegisterClick) {
                        Text("Don't have an account? Sign up")
                            .font(.caption)
                            .foregroundColor(logoTint)
                    }
                }
                .padding(24)
                .frame(maxWidth: 380)
                .background(cardColor)
                .cornerRadius(20)
                .shadow(color: .black.opacity(0.1), radius: 6, x: 0, y: 3)

                Spacer()
            }
            .padding(.horizontal, 16)
        }
    }
}
