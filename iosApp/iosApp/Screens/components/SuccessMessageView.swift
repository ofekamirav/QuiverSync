//
//  SuccessMessageView.swift
//  iosApp
//
//  Created by gal levi on 19/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct SuccessMessageView: View {
    let title: String
    let subtitle: String?
    let systemIconName: String
    let iconColor: Color
    let backgroundColor: Color
    let onDismiss: (() -> Void)?

    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: systemIconName)
                .resizable()
                .scaledToFit()
                .frame(width: 48, height: 48)
                .foregroundColor(iconColor)

            Text(title)
                .font(.title2)
                .fontWeight(.semibold)
                .foregroundColor(.primary)
                .multilineTextAlignment(.center)

            if let subtitle = subtitle {
                Text(subtitle)
                    .font(.body)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
            }

            if let onDismiss = onDismiss {
                Button(action: onDismiss) {
                    Text("OK")
                        .fontWeight(.medium)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 10)
                        .background(Capsule().fill(iconColor.opacity(0.15)))
                }
                .buttonStyle(.plain)
                .padding(.top, 8)
            }
        }
        .padding()
        .frame(maxWidth: .infinity)
        .background(backgroundColor.opacity(0.05))
        .cornerRadius(20)
        .shadow(radius: 5)
        .padding(.horizontal, 24)
        .padding(.vertical, 12)
        .transition(.opacity.combined(with: .scale))
        .animation(.spring(), value: UUID())
    }
}
