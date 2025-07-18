//
//  EmptyStateView.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct EmptyStateView: View {
    @Environment(\.colorScheme) var colorScheme

    let title: String
    let message: String
    let buttonText: String
    let systemImageName: String
    let onButtonTap: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Spacer()
            Image(systemName: systemImageName)
                .resizable()
                .scaledToFit()
                .frame(width: 80, height: 80)
                .foregroundColor(AppColors.surfBlue.opacity(0.6))
                .padding(.bottom, 8)

            Text(title)
                .font(.title)
                .fontWeight(.semibold)
                .foregroundColor(AppColors.textPrimary(for: colorScheme))

            Text(message)
                .font(.body)
                .multilineTextAlignment(.center)
                .foregroundColor(.secondary)
                .padding(.horizontal, 32)

            Button(action: onButtonTap) {
                Text(buttonText)
                    .fontWeight(.bold)
                    .padding(.vertical, 12)
                    .padding(.horizontal, 24)
                    .background(AppColors.surfBlue)
                    .foregroundColor(.white)
                    .cornerRadius(12)
            }
            Spacer()
        }
        .padding()
    }
}
