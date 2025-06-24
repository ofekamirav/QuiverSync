//
//  SpotCard.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

public struct SpotCard: View {
    
    let favSpot: FavoriteSpot
    
    @State private var isExpanded = false
    
    public var body: some View {
        Spacer()
        VStack(spacing:16){
            HStack{
                VStack{
                    Text("\(favSpot.name)")
                        .font(.headline)
                        .foregroundColor(AppColors.deepBlue)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    Text("\(favSpot.location)")
                        .font(.caption)
                        .foregroundColor(.gray)
                        .frame(maxWidth: .infinity, alignment: .leading)

                }
                
                
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
            .background(AppColors.foamWhite)
            
            if isExpanded {
                Divider()
                ExpendCardLoading(favSpot: favSpot)
            }
        }
        .padding()
        
    }
}
