//
//  ExpendCard.swift
//  iosApp
//
//  Created by gal levi on 21/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

public struct ExpendCard: View {
    let quiver: Quiver
    let forecast: WeeklyForecast
    let favSpot: FavoriteSpot

    var recommendedBoard: Surfboard? {
        quiver.items.first { $0.id == favSpot.recommendedBoardId }
    }

    public var body: some View {
        HStack(alignment: .top, spacing: 16) {
            // MARK: - Image + Confidence Chip
            VStack(alignment: .center, spacing: 8) {
                Image(recommendedBoard?.imageRes ?? "placeholder")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 72, height: 92)
                    .clipShape(RoundedRectangle(cornerRadius: 12))

                ChipView(
                    text: "\(Int(favSpot.confidence))% match",
                    color: AppColors.surfBlue.opacity(0.1)
                )
                .offset(y:5)
            }

            // MARK: - Info + Forecast
            VStack(alignment: .leading, spacing: 6) {
                Text(recommendedBoard?.type ?? "Unknown")
                    .font(.headline)
                    .foregroundColor(AppColors.deepBlue)

                Text(recommendedBoard?.model ?? "")
                    .font(.subheadline)
                    .foregroundColor(.gray)

                Text(forecast.list.first?.date ?? "")
                    .font(.caption)
                    .foregroundColor(.gray)

                HStack(spacing: 16) {
                    Label {
                        Text("\(forecast.list.first?.waveHeight ?? 0.0, specifier: "%.1f") m")
                    } icon: {
                        Image(systemName: "water.waves")
                    }

                    Label {
                        Text("\(forecast.list.first?.windSpeed ?? 0.0, specifier: "%.1f") m/s")
                    } icon: {
                        Image(systemName: "wind")
                    }
                }
                .font(.subheadline)
                .foregroundColor(AppColors.deepBlue)
                .padding(.top, 4)
                .fixedSize(horizontal: true, vertical: false)
                .layoutPriority(1)
                .offset(y:9)
            }
            .offset(y:15)
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }
}
