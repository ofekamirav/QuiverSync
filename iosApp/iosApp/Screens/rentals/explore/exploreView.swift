//
//  exploreView.swift
//  iosApp
//
//  Created by gal levi on 21/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct ExploreView: View {
    @Environment(\.colorScheme) var colorScheme

    let exploreData: [BoardForDisplay]

    // Adaptive grid layout
    private let columns = [
        GridItem(.adaptive(minimum: 320), spacing: 16)
    ]

    var body: some View {
        ScrollView {
            LazyVGrid(columns: columns, spacing: 16) {
                ForEach(exploreData, id: \.id) { offer in
                    RentalBoardCardView(rentalOffer: offer)
                        .frame(maxWidth: .infinity)

                }
            }
            .padding()
        }
        .background(AppColors.sectionBackground(for: colorScheme))
        
    }
}
