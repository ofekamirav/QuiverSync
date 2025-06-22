//
//  BoardRecommendationCard.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI

struct BoardRecommendationCardView: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Today's Board Recommendation")
                .font(.headline)
                .foregroundColor(AppColors.deepBlue)

            HStack(spacing: 16) {
                Image("OldFellow")
                    .resizable()
                    .frame(width: 78, height: 88)
                    .clipShape(RoundedRectangle(cornerRadius: 12))

                VStack(alignment: .leading, spacing: 4) {
                    Text("Shortboard")
                        .fontWeight(.bold)
                        .foregroundColor(AppColors.deepBlue)

                    Text("5'10\" x 19\" x 2.3\"")
                        .font(.caption)
                        .foregroundColor(.gray)

                    HStack(spacing: 8) {
                        ChipView(text: "Perfect Match", color: AppColors.skyBlue.opacity(0.1))
                        ChipView(text: "98% Confidence", color: AppColors.surfBlue.opacity(0.1))
                    }
                }
            }
        }
        .padding()
        .frame(maxWidth: .infinity)
        .background(AppColors.foamWhite)
        .cornerRadius(20)
    }
}
