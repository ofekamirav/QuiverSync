//
//  Untitled.swift
//  iosApp
//
//  Created by gal levi on 25/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared

public struct WeeklyForecastPopup: View {
    @Binding var selectedSpot : FavoriteSpot?
    let weeklyPrediction: WeeklyPrediction

    public var body: some View {
        ZStack {
            // Popup container
            VStack(alignment: .leading, spacing: 12) {
                // Header
                HStack {
                    Text("Weekly Forecast")
                        .font(.title)
                        .fontWeight(.bold)
                        .foregroundColor(AppColors.deepBlue)

                    Spacer()

                    Button(action: {
                        selectedSpot = nil
                    }) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title)
                            .foregroundColor(.gray)
                    }
                }
                .padding(.bottom, 8)
                

                Divider()

                // Lazy list of daily predictions
                ScrollView {
                    LazyVStack(spacing: 12) {
                        ForEach(weeklyPrediction.List, id: \.DailyForecast.date) { prediction in
                            ForecastRow(prediction: prediction)
                                .padding()
                                .background(AppColors.foamWhite)
                                .clipShape(RoundedRectangle(cornerRadius: 16))
                        }
                    }
                }
            }
            .padding()
            .padding(.horizontal)
        }
    }
}
