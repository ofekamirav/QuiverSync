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
    let favSpotData : FavSpotsData
    let favSpotsViewModel : FavSpotsViewModel
    let showPopup: () -> Void
    @Binding var selectedSpot: FavoriteSpot?
    
    
    public var body: some View {
        // First get the tuple with optional binding
        if let dailyData = getDailyPredictions(
            spot: favSpot,
            allSpotsDailyPredictions: favSpotData.allSpotsDailyPredictions,
            boards: favSpotData.boards,
            forecasts: favSpotData.currentForecastsForAllSpots
        ) {
            // If we have data, use it
            let (prediction, recommendedBoard, currentDayForecast) = dailyData
            
            let confidence = prediction.score
            
            VStack(spacing: 12) {
                // Main forecast content
                HStack(alignment: .top, spacing: 16) {
                    // 🟦 Image + Chip
                    VStack(alignment: .center, spacing: 8) {
                        AsyncImage(url: URL(string: recommendedBoard.imageRes)) { phase in
                            switch phase {
                            case .success(let image):
                                image
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                    .frame(width: 72, height: 92)
                            case .failure(_):
                                Image(systemName: "photo")
                                    .frame(width: 72, height: 92)
                            case .empty:
                                ProgressView()
                                    .frame(width: 72, height: 92)
                            @unknown default:
                                ProgressView()
                                    .frame(width: 72, height: 92)
                            }
                        }
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                        
                        ChipView(
                            text: "\(Int(confidence))% match",
                            color: AppColors.surfBlue.opacity(0.1)
                        )
                        .offset(y:5)
                    }
                    
                    // 🟨 Info
                    VStack(alignment: .leading, spacing: 6) {
                        Text(recommendedBoard.company)
                            .font(.headline)
                            .foregroundColor(AppColors.deepBlue)
                        
                        Text(recommendedBoard.model)
                            .font(.subheadline)
                            .foregroundColor(.gray)
                        
                        Text(currentDayForecast.date)
                            .font(.caption)
                            .foregroundColor(.gray)
                        
                        HStack(spacing: 16) {
                            Label {
                                Text("\(currentDayForecast.waveHeight, specifier: "%.1f") m")
                            } icon: {
                                Image(systemName: "water.waves")
                            }
                            
                            Label {
                                Text("\(currentDayForecast.windSpeed, specifier: "%.1f") m/s")
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
            
        } else {
            Group{
                // Fallback content
                Text("No forecast data available")
                    .foregroundColor(.gray)
                
                // Fallback content
                Text("No forecast data available")
                    .foregroundColor(.gray)
            }
            .onAppear(){
                favSpotsViewModel.onEvent(event: FavSpotsEventErrorOccurred(
                    message: "No forecast data available for this spot."
                ))
            }
        }
        
    }
}
