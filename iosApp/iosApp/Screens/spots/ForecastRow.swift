//
//  Untitled.swift
//  iosApp
//
//  Created by gal levi on 25/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI
import Shared

struct ForecastRow: View {
    
    let prediction: DailyPrediction

    var body: some View {
        HStack(alignment: .top, spacing: 16) {
            VStack(spacing: 8) {
                Image(prediction.Surfboard.imageRes)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 64, height: 80)
                    .clipShape(RoundedRectangle(cornerRadius: 12))

                ChipView(
                    text: "\(prediction.confidence)% match",
                    color: AppColors.surfBlue.opacity(0.1)
                )
            }

            VStack(alignment: .leading, spacing: 4) {
                Text(prediction.Surfboard.type)
                    .font(.headline)
                    .foregroundColor(AppColors.deepBlue)

                Text(prediction.Surfboard.model)
                    .font(.subheadline)
                    .foregroundColor(.gray)

                Text(prediction.DailyForecast.date)
                    .font(.caption)
                    .foregroundColor(.gray)

                HStack(spacing: 16) {
                    Label("\(prediction.DailyForecast.waveHeight, specifier: "%.1f") m", systemImage: "water.waves")
                    Label("\(prediction.DailyForecast.windSpeed, specifier: "%.1f") m/s", systemImage: "wind")
                }
                .font(.subheadline)
                .foregroundColor(AppColors.deepBlue)
                .padding(.top, 4)
            }
        }
    }
}
