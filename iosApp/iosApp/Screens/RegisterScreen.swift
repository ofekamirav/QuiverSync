//
//  RegisterScreen.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI

struct RegisterScreen: View {
    @Environment(\.colorScheme) var colorScheme

    @State private var name = ""
    @State private var email = ""
    @State private var password = ""

    let onSignUpClick: () -> Void

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

                    CustomInputField(label: "Name", text: $name, systemImage: "person")
                    CustomInputField(label: "Email", text: $email, systemImage: "envelope")
                    CustomInputField(label: "Password", text: $password, systemImage: "lock", isSecure: true)

                    ProfileImageUpload()

                    GradientButton(text: "Sign Up", action: onSignUpClick)

                    Button(action: {
                        onSignUpClick()
                    }) {
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

                Spacer()
            }
            .padding(.horizontal, 16)
        }
    }
}

#Preview {
    RegisterScreen(onSignUpClick: {})
}

