//
//  RegisterView.swift
//  iosApp
//
//  Created by gal levi on 01/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct RegisterView: View {
    @Environment(\.colorScheme) var colorScheme

    // MARK: - Input from parent
    let state: RegisterFormData
    let RegViewModel: RegisterViewModel

    // MARK: - Actions
    let onBackClick: () -> Void
    let onSuccess: () -> Void
    
    @State private var name: String = ""
    @State private var email: String = ""
    @State private var password: String = ""


    // MARK: - Theme
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

                    Text("Create Account")
                        .font(.headline)
                        .foregroundColor(textColor)

                    // MARK: - Name
                    CustomInputField(label: "Name", text: $name, systemImage: "person")
                        .onChange(of: name) { newValue in
                            RegViewModel.onEvent(event: RegisterEventNameChanged(value: newValue))
                        }
                    if let nameError = state.nameError {
                        Text(nameError)
                            .foregroundColor(.red)
                            .font(.caption)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 4)
                    }

                    // MARK: - Email
                    CustomInputField(label: "Email", text: $email, systemImage: "envelope")
                        .onChange(of: email) { newValue in
                            RegViewModel.onEvent(event: RegisterEventEmailChanged(value: newValue))
                            print(state)
                        }
                    if let emailError = state.emailError {
                        Text(emailError)
                            .foregroundColor(.red)
                            .font(.caption)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 4)
                    }

                    // MARK: - Password
                    CustomInputField(label: "Password", text: $password, systemImage: "lock", isSecure: true)
                        .onChange(of: password) { newValue in
                            RegViewModel.onEvent(event: RegisterEventPasswordChanged(value: newValue))
                            print(state)
                        }
                    if let passwordError = state.passwordError {
                        Text(passwordError)
                            .foregroundColor(.red)
                            .font(.caption)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 4)
                    }

                    // MARK: - Upload & Submit
                    ProfileImageUpload()

                    GradientButton(text: "Sign Up") {
                        RegViewModel.onEvent(event: RegisterEventSignUpClicked())
                    }

                    Button(action: onBackClick) {
                        Text("Already have an account? Sign in")
                            .font(.caption)
                            .foregroundColor(logoTint)
                    }
                }
                .padding(24)
                .frame(maxWidth: 380)
                .background(cardColor)
                .cornerRadius(20)
                .shadow(color: .black.opacity(0.1), radius: 6, x: 0, y: 3)
                .onAppear(){
                    name = state.name
                    email = state.email
                    password = state.password
                }

                Spacer()
            }
            .padding(.horizontal, 16)
        }
    }
}
