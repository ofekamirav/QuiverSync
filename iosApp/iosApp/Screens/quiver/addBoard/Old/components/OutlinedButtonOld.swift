//
//  OutlinedButton.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI

struct OutlinedButtonOld: View {
    let text: String
    let icon: String?        // Optional system icon name (e.g., "arrow.right")
    let isEnabled: Bool
    let fullWidth: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack {
                if let icon = icon {
                    Image(systemName: icon)
                        .font(.body)
                }
                Text(text)
                    .fontWeight(.medium)
            }
            .padding()
            .frame(maxWidth: fullWidth ? .infinity : nil)
            .foregroundColor(isEnabled ? .accentColor : .gray)
            .overlay(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(isEnabled ? Color.accentColor : Color.gray, lineWidth: 1.5)
            )
            .opacity(isEnabled ? 1.0 : 0.5)
        }
        .disabled(!isEnabled)
    }
}
