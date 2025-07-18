//
//  ForgotPasswordSuccessView.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct ForgotPasswordSuccessView: View {
    let message: String
    let onLoginClick: () -> Void

    var body: some View {
        VStack(spacing: 24) {
            // Checkmark icon
            ZStack {
                Circle()
                    .fill(Color.accentColor)
                    .frame(width: 64, height: 64)

                Image(systemName: "checkmark")
                    .foregroundColor(.white)
                    .font(.system(size: 32, weight: .bold))
            }

            // Title
            Text("Check Your Inbox")
                .font(.title)
                .fontWeight(.bold)

            // Message
            Text(message)
                .font(.subheadline)
                .multilineTextAlignment(.center)
                .foregroundColor(.gray)

            // Back to login button
            Button(action: onLoginClick) {
                Text("Back to Login")
                    .foregroundColor(.white)
                    .fontWeight(.semibold)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.accentColor)
                    .cornerRadius(12)
            }
        }
        .padding()
    }
}
