//
//  ForgotPasswordView.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct ForgotPasswordView: View {
    let formData: ForgotPasswordFormData
    let onEvent: (ForgotPasswordEvent) -> Void

    @State private var email: String = ""
    
    var body: some View {
        VStack(spacing: 24) {
            Text("Reset Your Password")
                .font(.title)
                .fontWeight(.bold)

            Text("Enter your account's email and we’ll send you a reset link.")
                .font(.subheadline)
                .multilineTextAlignment(.center)
                .foregroundColor(.gray)

            CustomInputField(
                label: "Email",
                text: $email,
                systemImage: "envelope"
            )
            .keyboardType(.emailAddress)
            .autocapitalization(.none)
            .onChange(of: email) {
                onEvent(ForgotPasswordEvent.EmailChanged(email: $0))
            }

            if let error = formData.emailError {
                Text(error)
                    .foregroundColor(.red)
                    .font(.caption)
                    .transition(.opacity)
            }

            Button(action: {
                onEvent(ForgotPasswordEvent.SendResetLink())
            }) {
                Text("Send Reset Link")
                    .foregroundColor(.white)
                    .fontWeight(.semibold)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.accentColor)
                    .cornerRadius(12)
            }
        }
        .padding()
        .onAppear {
            email = formData.email
        }
    }
}
