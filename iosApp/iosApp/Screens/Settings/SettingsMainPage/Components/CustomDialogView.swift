//
//  CustomDialogView.swift
//  iosApp
//
//  Created by gal levi on 16/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct CustomDialogView: View {
    @Environment(\.colorScheme) var colorScheme

    let title: String
    let message: String
    let onConfirm: () -> Void
    let onCancel: () -> Void

    var body: some View {
        ZStack {
            Color.black.opacity(0.4)
                .ignoresSafeArea()

            VStack(spacing: 16) {
                Text(title)
                    .font(.title3)
                    .fontWeight(.semibold)
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))

                Text(message)
                    .font(.body)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)

                HStack(spacing: 12) {
                    Button(action: onCancel) {
                        Text("Cancel")
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(
                                AppColors.chipBackground(for: colorScheme)
                            )
                            .foregroundColor(AppColors.textPrimary(for: colorScheme))
                            .cornerRadius(10)
                    }

                    Button(action: onConfirm) {
                        Text("Confirm")
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(AppColors.error)
                            .foregroundColor(.white)
                            .cornerRadius(10)
                    }
                }
            }
            .padding(24)
            .background(AppColors.cardColor(for: colorScheme))
            .cornerRadius(16)
            .shadow(radius: 10)
            .padding(.horizontal, 40)
        }
        .transition(.opacity)
        .animation(.easeInOut(duration: 0.3), value: UUID())
    }
}
