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
    
    
    public var body: some View {
        ScrollView{
            LazyVStack{
                ForEach(favSpots.items, id: \.id){ favSpot in
                    SpotCard(favSpot: favSpot)
                        .background(AppColors.foamWhite)
                        .clipShape(RoundedRectangle(cornerRadius: 20))
                        .shadow(color: .black.opacity(0.05), radius: 5, x: 0, y: 2)
                        .padding(.horizontal)
                }
            }
        }
        .background(AppColors.background)
    }
}
