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
    @Environment(\.colorScheme) var colorScheme

    @Binding var selectedSpot : FavoriteSpot?
    let favSpotsData: FavSpotsData
    let favSpotsViewModel : FavSpotsViewModel
    
    public var body: some View {
        
        if let spot = selectedSpot {
            

            let weeklyPrediction = favSpotsData.weeklyUiPredictions
            
            
            ZStack {
                VStack(alignment: .leading, spacing: 12) {
                    // Header
                    HStack {
                        Text("Weekly Forecast")
                            .font(.title)
                            .fontWeight(.bold)
                            .foregroundColor(AppColors.textPrimary(for: colorScheme))

                        Spacer()

                        Button(action: {
                            selectedSpot = nil
                        }) {
                            Image(systemName: "xmark.circle.fill")
                                .font(.title)
                                .foregroundColor(colorScheme == .dark ? AppColors.darkSky : .gray)
                        }
                    }
                    .padding(.bottom, 8)
                    Text("\(spot.name)")
                        .font(.subheadline)
                        .foregroundColor(colorScheme == .dark ? AppColors.darkSky : .gray)

                    Divider()

                    // Scrollable forecast rows
                    ScrollView {
//                        LazyVStack(spacing: 12) {
                            ForEach(weeklyPrediction, id: \.dailyForecast.date) { prediction in

                                ForecastRow(prediction: prediction)
                                    .padding()
                                    .background(AppColors.cardColor(for: colorScheme))
                                    .clipShape(RoundedRectangle(cornerRadius: 16))
                            }
//                        }
//                        .padding(.bottom, 24) // Add spacing below last card
                    }


                    Spacer(minLength: 0) // Prevent clipping
                }
                .padding()
                .padding(.horizontal)
            }
        }else{
            FavSpotsScreen()
        }
    }
}
