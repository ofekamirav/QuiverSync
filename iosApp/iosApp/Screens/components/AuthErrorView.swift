//
//  AuthErrorView.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct AuthErrorView: View {
    @Environment(\.colorScheme) var colorScheme

    let title: String
    let message: String
    let primaryButtonText: String
    let onPrimaryTap: () -> Void
    let secondaryButtonText: String?
    let onSecondaryTap: (() -> Void)?

    var body: some View {
        VStack(spacing: 20) {
            Spacer()

            Image(systemName: "exclamationmark.shield.fill")
                .resizable()
                .scaledToFit()
                .frame(width: 80, height: 80)
                .foregroundColor(AppColors.sandOrange)
                .padding(.bottom, 4)

            Text(title)
                .font(.title2)
                .fontWeight(.bold)
                .foregroundColor(AppColors.textPrimary(for: colorScheme))

            Text(message)
                .multilineTextAlignment(.center)
                .foregroundColor(.secondary)
                .padding(.horizontal, 32)

            // Primary button
            Button(action: onPrimaryTap) {
                Text(primaryButtonText)
                    .fontWeight(.bold)
                    .padding(.vertical, 12)
                    .padding(.horizontal, 28)
                    .background(AppColors.surfBlue)
                    .foregroundColor(.white)
                    .cornerRadius(16)
            }

            // Secondary button
            if let secondaryText = secondaryButtonText, let onTap = onSecondaryTap {
                Button(action: onTap) {
                    Text(secondaryText)
                        .foregroundColor(AppColors.textPrimary(for: colorScheme))
                        .underline()
                }
            }

            Spacer()
        }
        .padding()
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(AppColors.sectionBackground(for: colorScheme))
        .edgesIgnoringSafeArea(.all)
    }
}
