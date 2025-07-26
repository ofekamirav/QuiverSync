//
//  SmartMatchBtn.swift
//  iosApp
//
//  Created by gal levi on 26/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct SmartMatchBtn: View {
    @Binding var showForecastOnly: Bool
    let colorScheme: ColorScheme

    var body: some View {
        HStack(spacing: 0) {
            Button(action: { withAnimation { showForecastOnly = false } }) {
                Text("Smart Match")
                    .font(.subheadline.weight(.medium))
                    .foregroundColor(showForecastOnly ? AppColors.textPrimary(for: colorScheme).opacity(0.5) : .white)
                    .padding(.vertical, 10)
                    .frame(maxWidth: .infinity)
                    .background(showForecastOnly ? Color.clear : AppColors.deepBlue)
            }

            Button(action: { withAnimation { showForecastOnly = true } }) {
                Text("Forecast Only")
                    .font(.subheadline.weight(.medium))
                    .foregroundColor(showForecastOnly ? .white : AppColors.textPrimary(for: colorScheme).opacity(0.5))
                    .padding(.vertical, 10)
                    .frame(maxWidth: .infinity)
                    .background(showForecastOnly ? AppColors.deepBlue : Color.clear)
            }
        }
        .background(AppColors.cardColor(for: colorScheme).opacity(0.9))
        .clipShape(Capsule())
        .padding(.horizontal)
        .padding(.top, 8)
    }
}
