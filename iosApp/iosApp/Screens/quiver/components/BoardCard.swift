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
    let onPublishToggle: (Surfboard, Bool) -> Void

    @State private var toggleValue: Bool

    init(board: Surfboard, onClick: @escaping () -> Void, onPublishToggle: @escaping (Surfboard, Bool) -> Void) {
        self.board = board
        self.onClick = onClick
        self.onPublishToggle = onPublishToggle
        _toggleValue = State(initialValue: board.isRentalPublished?.boolValue ?? false)
    }

    var body: some View {
        Button(action: onClick) {
            VStack(spacing: 12) {
                AsyncImage(
                    url: URL(string: board.imageRes ?? ""),
                    content: { image in
                        image
                            .resizable()
                            .scaledToFit()
                    },
                    placeholder: {
                        Image(systemName: "photo")
                            .resizable()
                            .scaledToFit()
                            .foregroundColor(.gray.opacity(0.3))
                    }
                )
                .frame(height: 100)
                .cornerRadius(10)

                Text(board.model)
                    .font(.headline)
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                    .lineLimit(1)

                HStack {
                    Text("For Rent")
                        .font(.caption)
                        .foregroundColor(AppColors.chipText(for: colorScheme).opacity(0.8))

                    Spacer()

                    Toggle("", isOn: $toggleValue)
                        .labelsHidden()
                        .onChange(of: toggleValue) { newValue in
                            onPublishToggle(board, newValue)
                        }
                }
            }
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 20)
                    .fill(AppColors.cardColor(for: colorScheme))
                    .shadow(color: colorScheme == .dark ? .clear : .black.opacity(toggleValue ? 0.1 : 0.05), radius: toggleValue ? 6 : 3)
                    .overlay(
                        RoundedRectangle(cornerRadius: 20)
                            .stroke(toggleValue ? AppColors.sandOrange : Color.clear, lineWidth: 2)
                    )
            )
        }
    }
}
