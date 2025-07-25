//
//  BoardCard.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct BoardCard: View {
    @Environment(\.colorScheme) var colorScheme

    let board: Surfboard
    let onClick: () -> Void
    @Binding var isPublished: Bool

    var body: some View {
        Button(action: onClick) {
            VStack(spacing: 12) {
                
                AsyncImage(url: URL(string: board.imageRes ?? "")) { phase in
                    switch phase {
                    case .success(let image):
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: 110, height: 130)
                            .clipped()
                            .cornerRadius(16)
                    default:
                        Rectangle()
                            .fill(Color.gray.opacity(0.2))
                            .frame(width: 110, height: 100)
                            .cornerRadius(16)
                    }
                }


                Text(board.model)
                    .font(.headline)
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                    .lineLimit(1)

                HStack {
                    Text("For Rent")
                        .font(.caption)
                        .foregroundColor(AppColors.chipText(for: colorScheme).opacity(0.8))

                    Spacer()

                    Toggle("", isOn: $isPublished)
                        .labelsHidden()
                }
            }
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 20)
                    .fill(AppColors.cardColor(for: colorScheme))
                    .shadow(color: colorScheme == .dark ? .clear : .black.opacity(isPublished ? 0.1 : 0.05), radius: isPublished ? 6 : 3)
                    .overlay(
                        RoundedRectangle(cornerRadius: 20)
                            .stroke(isPublished ? AppColors.sandOrange : Color.clear, lineWidth: 2)
                    )
            )
        }
    }
}
