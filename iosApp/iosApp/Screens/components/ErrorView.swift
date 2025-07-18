//
//  ErrorView.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

//  ErrorView.swift
//  iosApp

import SwiftUI

struct ErrorView: View {
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.dismiss) var dismiss

    let title: String
    let message: String
    let systemImageName: String
    let buttonText: String
    let onRetry: (() -> Void)?

    var body: some View {
        VStack(spacing: 20) {
            Spacer()

            Image(systemName: systemImageName)
                .resizable()
                .scaledToFit()
                .frame(width: 80, height: 80)
                .foregroundColor(AppColors.surfBlue.opacity(0.7))
                .padding(.bottom, 8)

            Text(title)
                .font(.title2)
                .fontWeight(.bold)
                .foregroundColor(AppColors.textPrimary(for: colorScheme))

            Text(message)
                .font(.body)
                .multilineTextAlignment(.center)
                .foregroundColor(.secondary)
                .padding(.horizontal, 32)

            Button(action: {
                if let retry = onRetry {
                    retry()
                } else {
                    dismiss()
                }
            }) {
                Text(buttonText)
                    .fontWeight(.bold)
                    .padding(.vertical, 12)
                    .padding(.horizontal, 28)
                    .background(AppColors.sandOrange)
                    .foregroundColor(.white)
                    .cornerRadius(16)
            }

            Spacer()
        }
        .padding()
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(AppColors.sectionBackground(for: colorScheme))
        .edgesIgnoringSafeArea(.all)
    }
}
