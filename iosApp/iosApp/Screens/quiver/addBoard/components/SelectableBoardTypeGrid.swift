//
//  SelectableBoardTypeGrid.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared

struct SelectableBoardTypeGrid: View {
    let selectedType: SurfboardType
    let onTypeSelected: (SurfboardType) -> Void

    let allTypes: [SurfboardType] = SurfboardType.allCases

    let columns: [GridItem] = [
        GridItem(.flexible(), spacing: 12),
        GridItem(.flexible(), spacing: 12)
    ]

    var body: some View {
        LazyVGrid(columns: columns, spacing: 12) {
            ForEach(allTypes, id: \.self) { type in
                let isSelected = selectedType == type

                Text(type.serverName)
                    .font(.subheadline)
                    .foregroundColor(isSelected ? .white : .primary)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
                    .background(
                        RoundedRectangle(cornerRadius: 10)
                            .fill(isSelected ? Color.blue : Color.gray.opacity(0.2))
                    )
                    .overlay(
                        RoundedRectangle(cornerRadius: 10)
                            .stroke(isSelected ? Color.blue : Color.clear, lineWidth: 2)
                    )
                    .onTapGesture {
                        onTypeSelected(type)
                    }
            }
        }
    }
}

