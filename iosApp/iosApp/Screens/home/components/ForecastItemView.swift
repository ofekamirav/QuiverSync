//
//  ForecastItemView.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI

struct ForecastItemView: View {
    @Environment(\.colorScheme) var colorScheme

    let day: String
    let date: String
    let waveHeight: String
    let wind: String

    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(day)
                    .fontWeight(.bold)
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                Text(date)
                    .font(.caption)
                    .foregroundColor(.gray)
            }
            Spacer()

            HStack(spacing: 8) {
                Image(systemName: "water.waves")
                Text(waveHeight)
            }
            .foregroundColor(AppColors.textPrimary(for: colorScheme))

            Spacer().frame(width: 16)

            HStack(spacing: 8) {
                Image(systemName: "wind")
                Text(wind)
            }
            .foregroundColor(AppColors.textPrimary(for: colorScheme))
        }
        .padding()
        .background(AppColors.cardColor(for: colorScheme))
        .cornerRadius(16)
    }
}
