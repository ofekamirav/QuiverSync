//
//  QuiverView.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI
import Shared

struct QuiverView: View {
    let quiver : Quiver
    let onBoardTap: (Surfboard) -> Void
    
    let columns = Array(repeating: GridItem(.flexible(), spacing: 16), count: 2)
    
    var body: some View {
        ScrollView{
            LazyVGrid(columns: columns, spacing: 16){
                ForEach(quiver.items, id: \.id){board in
                    SurfboardCard(board: board){
                        onBoardTap(board)
                    }
                }
            }
            .padding()
        }
        .background(AppColors.background)
    }
}
