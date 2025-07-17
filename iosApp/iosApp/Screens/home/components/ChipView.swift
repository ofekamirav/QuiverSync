//
//  ChipView.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI

struct ChipView: View {
    @Environment(\.colorScheme) var colorScheme

    let text: String
    let color: Color

    var body: some View {
        Text(text)
            .font(.caption)
            .foregroundColor(AppColors.textPrimary(for: colorScheme))
            .padding(.horizontal, 12)
            .padding(.vertical, 4)
            .background(color)
            .cornerRadius(12)
    }
}
