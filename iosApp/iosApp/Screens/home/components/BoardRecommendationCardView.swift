//
//  BoardRecommendationCard.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared

struct BoardRecommendationCardView: View {
    @Environment(\.colorScheme) var colorScheme

    let surfboard: Surfboard
    let prediction: GeminiPrediction
    var onAddBoardTapped: (() -> Void)? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Today's Board Recommendation")
                .font(.headline)
                .foregroundColor(AppColors.textPrimary(for: colorScheme))

            if surfboard.id != "default" {
                HStack(spacing: 16) {
                    AsyncImage(
                        url: URL(string: surfboard.imageRes),
                        content: { image in
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                        },
                        placeholder: {
                            Image(systemName: "photo")
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .foregroundColor(.gray.opacity(0.3))
                        }
                    )
                    .frame(width: 78, height: 88)
                    .clipShape(RoundedRectangle(cornerRadius: 12))

                    VStack(alignment: .leading, spacing: 4) {
                        Text(surfboard.type.serverName)
                            .fontWeight(.bold)
                            .foregroundColor(AppColors.textPrimary(for: colorScheme))

                        Text("\(surfboard.height) x \(surfboard.width) – \(surfboard.volume)")
                            .font(.caption)
                            .foregroundColor(.gray)

                        HStack(spacing: 8) {
                            ChipView(
                                text: getMatchLabel(for: Int(prediction.score)),
                                color: AppColors.skyBlue.opacity(0.1)
                            )
                            ChipView(
                                text: "\(prediction.score)% Match",
                                color: AppColors.surfBlue.opacity(0.1)
                            )
                        }
                    }
                }
            } else {
                HStack(spacing: 16) {
                    Image(systemName: "plus.circle.fill")
                        .resizable()
                        .foregroundColor(AppColors.skyBlue)
                        .frame(width: 78, height: 78)

                    VStack(alignment: .leading, spacing: 8) {
                        Text("No boards yet?")
                            .fontWeight(.bold)
                            .foregroundColor(AppColors.textPrimary(for: colorScheme))

                        Text("Tap here to add your first board!")
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                }
                .onTapGesture {
                    onAddBoardTapped?()
                }
            }
        }
        .padding()
        .frame(maxWidth: .infinity)
        .background(AppColors.cardColor(for: colorScheme))
        .cornerRadius(20)
    }

    private func getMatchLabel(for score: Int) -> String {
        switch score {
        case 95...:
            return "Perfect Match"
        case 90..<95:
            return "Good Match"
        case 80..<90:
            return "Alright Match"
        case 70..<80:
            return "Better to Rent"
        default:
            return "Not Recommended"
        }
    }
}
