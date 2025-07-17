//
//  SecurityAndPrivacyView.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct SecurityAndPrivacyView: View {
    let form: SecurityAndPrivacyFormData
    let onEvent: (SecurityAndPrivacyEvent) -> Void
    @Binding var showToast: Bool
    @Binding var navigateToProfile: Bool

    @State private var currentPassword: String = ""
    @State private var newPassword: String = ""
    @State private var confirmPassword: String = ""

    var body: some View {
        VStack(spacing: 24) {
            // Current Password
            CustomInputField(
                label: "Current Password",
                text: $currentPassword,
                systemImage: "lock",
                isSecure: true
            )
            .onChange(of: currentPassword) {
                onEvent(SecurityAndPrivacyEventOnCurrentPasswordChange(currentPassword: $0))
            }

            // New Password
            CustomInputField(
                label: "New Password",
                text: $newPassword,
                systemImage: "lock.rotation",
                isSecure: true
            )
            .onChange(of: newPassword) {
                onEvent(SecurityAndPrivacyEventOnNewPasswordChange(newPassword: $0))
            }

            // Confirm Password
            CustomInputField(
                label: "Confirm New Password",
                text: $confirmPassword,
                systemImage: "lock.shield",
                isSecure: true
            )
            .onChange(of: confirmPassword) {
                onEvent(SecurityAndPrivacyEventOnConfirmPasswordChange(confirmPassword: $0))
            }

            // Error Text
            if let error = form.error {
                Text(error)
                    .foregroundColor(.red)
                    .font(.footnote)
                    .padding(.horizontal)
                    .transition(.opacity)
            }

            Spacer()

            Button(action: {
                onEvent(SecurityAndPrivacyEventOnChangePasswordClicked())
            }) {
                Text("Save New Password")
                    .foregroundColor(.white)
                    .fontWeight(.semibold)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.accentColor)
                    .cornerRadius(12)
            }
            .disabled(form.isPasswordLoading)
        }
        .padding()
        .onAppear {
            // Initial sync with form state
            currentPassword = form.currentPassword
            newPassword = form.newPassword
            confirmPassword = form.confirmPassword
        }
    }
}
