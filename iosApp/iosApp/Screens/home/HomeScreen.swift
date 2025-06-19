//
//  HomeScreen.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI

struct HomeScreen: View {
    @Environment(\.colorScheme) var colorScheme
    @State private var isExpanded = true

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                // Location header
                HStack {
                    Image(systemName: "location.fill")
                        .foregroundColor(AppColors.sandOrange)
                        .font(.system(size: 14))

                    Text("Pipeline, North Shore")
                        .font(.title3)
                        .foregroundColor(AppColors.deepBlue)
                        .fontWeight(.semibold)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    Button(action: {
                        withAnimation(
                            .easeInOut(duration: 0.3)){
                                isExpanded.toggle()
                            }
                         }) {
                        Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                            .foregroundColor(.gray)
                    }
                }
                .padding(.vertical, 8)
                .padding(.horizontal)

                // Current Conditions
                if isExpanded {
                    CurrentConditionsView(
                        waveHeight: "3-4ft",
                        wind: "15mph",
                        tide: "Rising"
                    )
                }

                BoardRecommendationCardView()

                Text("Weekly Forecast")
                    .font(.subheadline)
                    .foregroundColor(AppColors.deepBlue)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal)

                VStack(spacing: 12) {
                    ForecastItemView(day: "Mon", date: "Jan 15", waveHeight: "3-4ft", wind: "15mph")
                    ForecastItemView(day: "Tue", date: "Jan 16", waveHeight: "4-5ft", wind: "12mph")
                }
            }
            .padding()
        }
        .background(AppColors.background)
    }
}

#Preview{
    HomeScreen()
}
