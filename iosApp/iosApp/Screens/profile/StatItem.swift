//
//  StatItem.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared


public struct StatRow: View {

    let rentals = 5
    let boards = 3
    let spots = 3
    
    public var body: some View {
            HStack(spacing:0){
                StatItem(label: "Rentals", value: rentals)
                Divider()
                StatItem(label: "Boards", value: boards)
                Divider()
                StatItem(label: "Spots", value: spots)
            }
            .padding()
            .background(AppColors.foamWhite)
            .cornerRadius(12)
            .shadow(radius: 2)
            .frame(height: 100)
            .padding(.horizontal)
    }
}


public struct StatItem: View {
    let label: String
    let value: Int
    
    public var body: some View {
        VStack {
            Text("\(value)")
                .font(.headline)
                .foregroundColor(AppColors.deepBlue)
            Text(label)
                .font(.caption)
                .foregroundColor(.gray)
        }
        .frame(maxWidth:.infinity)
    }
}
