//
//  SettingItemView.swift
//  iosApp
//
//  Created by gal levi on 16/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct SettingItemView: View {
    @Environment(\.colorScheme) var colorScheme

    let iconName: String
    let label: String
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            HStack(spacing: 12) {
                Image(systemName: iconName)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 20, height: 20)
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))

                Text(label)
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                    .font(.system(size: 16, weight: .medium))

                Spacer()
                Image(systemName: "chevron.right")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(.gray)
            }
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(AppColors.cardColor(for: colorScheme))
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
}
