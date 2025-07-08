//
//  FavSpotsView.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared


public struct FavSpotsView: View {
    let favSpots : FavoriteSpots
    @State private var selectedSpot: FavoriteSpot? = nil

    public var body: some View {
        ZStack {
            // Background content: scrollable list of spot cards
            ScrollView {
                LazyVStack {
                    ForEach(favSpots.items, id: \.id) { favSpot in
                        SpotCard(favSpot: favSpot, selectedSpot: $selectedSpot)
                            .background(AppColors.foamWhite)
                            .clipShape(RoundedRectangle(cornerRadius: 20))
                            .shadow(color: .black.opacity(0.05), radius: 5, x: 0, y: 2)
                            .padding(.horizontal)
                    }
                }
            }
            .blur(radius: selectedSpot != nil ? 10 : 0) // Optional: blur background when popup is shown

            // Overlay popup
            if let spot = selectedSpot {
                WeeklyForecastPopup(selectedSpot: $selectedSpot, weeklyPrediction: spot.weeklyPrediction)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                    .zIndex(1)
            }
        }
        .animation(.easeInOut, value: selectedSpot != nil)
        .background(AppColors.background)
    }
}
