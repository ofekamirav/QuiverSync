//
//  ExtendCard.swift
//  iosApp
//
//  Created by gal levi on 25/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared

public struct ExtendCard: View {
    let favSpot : FavoriteSpot
    let showPopup: () -> Void
    @Binding var selectedSpot: FavoriteSpot?


    public var body: some View {
        let recommendedBoard = favSpot.weeklyPrediction.List.first?.Surfboard
        let currentDayForecast = favSpot.weeklyPrediction.List.first?.DailyForecast
        let confidence = favSpot.weeklyPrediction.List.first?.confidence ?? 0

        VStack(spacing: 12) {
            // Main forecast content
            HStack(alignment: .top, spacing: 16) {
                // 🟦 Image + Chip
                VStack(alignment: .center, spacing: 8) {
                    Image(recommendedBoard?.imageRes ?? "placeholder")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 72, height: 92)
                        .clipShape(RoundedRectangle(cornerRadius: 12))

                    ChipView(
                        text: "\(Int(confidence))% match",
                        color: AppColors.surfBlue.opacity(0.1)
                    )
                    .offset(y:5)
                }

                // 🟨 Info
                VStack(alignment: .leading, spacing: 6) {
                    Text(recommendedBoard?.type ?? "Unknown")
                        .font(.headline)
                        .foregroundColor(AppColors.deepBlue)

                    Text(recommendedBoard?.model ?? "")
                        .font(.subheadline)
                        .foregroundColor(.gray)

                    Text(currentDayForecast?.date ?? "")
                        .font(.caption)
                        .foregroundColor(.gray)

                    HStack(spacing: 16) {
                        Label {
                            Text("\(currentDayForecast?.waveHeight ?? 0.0, specifier: "%.1f") m")
                        } icon: {
                            Image(systemName: "water.waves")
                        }

                        Label {
                            Text("\(currentDayForecast?.windSpeed ?? 0.0, specifier: "%.1f") m/s")
                        } icon: {
                            Image(systemName: "wind")
                        }
                    }
                    .font(.subheadline)
                    .foregroundColor(AppColors.deepBlue)
                    .padding(.top, 4)
                    .offset(y:9)
                }
                .offset(y:15)
            }

            // 🟪 "View All Week" Button — nice separation
            Button(action: {
                showPopup()
                selectedSpot = favSpot
            }) {
                Text("View All Week Forecast")
                    .font(.subheadline)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(AppColors.surfBlue.opacity(0.2))
                    .foregroundColor(AppColors.deepBlue)
                    .cornerRadius(12)
            }
            .padding(.top, 4)
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }
}
