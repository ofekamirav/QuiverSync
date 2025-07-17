//
//  StatItem.swift
//  iosApp
//
//  Created by gal levi on 15/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI

struct StatItem: View {
    @Environment(\.colorScheme) var colorScheme

    let label: String
    let value: String

    var body: some View {
        VStack(spacing: 4) {
            Text(value)
                .font(.system(size: 16, weight: .bold))
                .foregroundColor(AppColors.textPrimary(for: colorScheme))

            Text(label)
                .font(.system(size: 13))
                .foregroundColor(AppColors.chipText(for: colorScheme).opacity(0.7))
        }
        .frame(maxWidth: .infinity)
    }
}
