//
//  SurfboardTypeCard.swift
//  iosApp
//
//  Created by gal levi on 25/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct SurfboardTypeCard: View {
    let type: SurfboardType
    let isSelected: Bool
    let onTap: () -> Void

    var body: some View {
        let imageUrl = getDefaultImageUrlForType(type: type)

        Button(action: onTap) {
            VStack(spacing: 8) {
                AsyncImage(url: URL(string: imageUrl)) { phase in
                    switch phase {
                    case .success(let image):
                        image
                            .resizable()
                            .scaledToFit()
                            .frame(height: 100)
                    default:
                        Color.gray.opacity(0.2)
                            .frame(height: 100)
                    }
                }

                Text(type.name.capitalized)
                    .font(.subheadline)
                    .foregroundColor(.primary)
            }
            .padding()
            .frame(maxWidth: .infinity)
            .background(isSelected ? Color.blue.opacity(0.2) : Color.gray.opacity(0.05))
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(isSelected ? Color.blue : Color.clear, lineWidth: 2)
            )
        }
    }
}
