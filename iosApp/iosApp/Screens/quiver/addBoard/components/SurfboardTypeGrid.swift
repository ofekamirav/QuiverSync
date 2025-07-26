//
//  SurfboardTypeGrid.swift
//  iosApp
//
//  Created by gal levi on 25/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct SurfboardTypeGrid: View {
    let selectedType: SurfboardType
    let onSelected: (SurfboardType) -> Void

    let columns = [
        GridItem(.flexible()),
        GridItem(.flexible())
    ]

    var body: some View {
        ScrollView{
            LazyVGrid(columns: columns, spacing: 16) {
                ForEach(SurfboardType.allCases, id: \.self) { type in
                    SurfboardTypeCard(
                        type: type,
                        isSelected: type == selectedType,
                        onTap: { onSelected(type) }
                    )
                }
            }
            .padding(.top, 8)
        }
    }
}
