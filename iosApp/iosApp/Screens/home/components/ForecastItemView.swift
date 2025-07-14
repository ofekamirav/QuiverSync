//
//  ForecastItemView.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI

struct ForecastItemView: View {
    let day: String
    let date: String
    let waveHeight: String
    let wind: String

    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(day)
                    .fontWeight(.bold)
                    .foregroundColor(AppColors.deepBlue)
                Text(date)
                    .font(.caption)
                    .foregroundColor(.gray)
            }
            Spacer()

            HStack(spacing: 8) {
                Image(systemName: "water.waves")
                Text(waveHeight)
            }
            .foregroundColor(AppColors.deepBlue)

            Spacer().frame(width: 16)

            HStack(spacing: 8) {
                Image(systemName: "wind")
                Text(wind)
            }
            .foregroundColor(AppColors.deepBlue)
        }
        .padding()
        .background(AppColors.foamWhite)
        .cornerRadius(16)
    }
}
