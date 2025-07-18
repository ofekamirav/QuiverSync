//  ForecastRow.swift
//  iosApp
//
//  Created by gal levi on 25/06/2025.
//  Copyright © 2025 orgName. All rights reserved.

import SwiftUI
import Shared

struct ForecastRow: View {
    @Environment(\.colorScheme) var colorScheme


    let prediction: DailyPrediction

    var body: some View {
        HStack(alignment: .top, spacing: 16) {
            VStack(spacing: 10) {
                AsyncImage(url: URL(string: prediction.surfboard.imageRes)) { phase in
                    switch phase {
                    case .success(let image):
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: 64, height: 80)
                            .clipped()
                    case .failure(_):
                        Image(systemName: "photo")
                            .resizable()
                            .frame(width: 64, height: 80)
                            .foregroundColor(.gray.opacity(0.6))
                    case .empty:
                        ProgressView()
                            .frame(width: 64, height: 80)
                    @unknown default:
                        ProgressView()
                            .frame(width: 64, height: 80)
                    }
                }
                .clipShape(RoundedRectangle(cornerRadius: 12))
                .shadow(radius: 2)

                ChipView(
                    text: "\(prediction.prediction.score)% match",
                    color: AppColors.chipBackground(for: colorScheme)
                )
                .padding(.top, 4)
            }

            VStack(alignment: .leading, spacing: 6) {
                Text(prediction.surfboard.company)
                    .font(.system(.headline, design: .rounded))
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))

                Text(prediction.surfboard.model)
                    .font(.subheadline)
                    .foregroundColor(colorScheme == .dark ? AppColors.darkSky : .gray)

                Text(prediction.dailyForecast.date)
                    .font(.caption)
                    .foregroundColor(colorScheme == .dark ? AppColors.darkSky.opacity(0.7) : .gray.opacity(0.8))

                HStack(spacing: 16) {
                    Label("\(prediction.dailyForecast.waveHeight, specifier: "%.1f") m", systemImage: "water.waves")
                    Label("\(prediction.dailyForecast.windSpeed, specifier: "%.1f") m/s", systemImage: "wind")
                }
                .font(.system(size: 14, weight: .medium, design: .rounded))
                .foregroundColor(AppColors.textPrimary(for: colorScheme))
                .padding(.top, 6)
            }
            Spacer()
        }
        .padding(.vertical, 8)
    }
}
