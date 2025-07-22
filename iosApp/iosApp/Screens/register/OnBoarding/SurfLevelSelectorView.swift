//
//  SurfLevelSelectorView.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct SurfLevelSelectorView: View {
    @Environment(\.colorScheme) var colorScheme

    let selectedLevel: SurfLevel?
    let onSelect: (SurfLevel) -> Void
    let error: String?
    
    private var levels: [SurfLevel] {
            SurfLevel.companion.all
        }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(spacing: 12) {
                ForEach(levels, id: \.self) { level in
                    Text(level.label)
                        .font(.caption)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 8)
                        .background(
                            RoundedRectangle(cornerRadius: 12)
                                .fill(level == selectedLevel ? AppColors.textPrimary(for: colorScheme) : Color.gray.opacity(0.2))
                        )
                        .foregroundColor(level == selectedLevel ? .white : .primary)
                        .onTapGesture {
                            onSelect(level)
                        }
                }
            }

            if let error = error {
                Text(error)
                    .font(.caption)
                    .foregroundColor(.red)
            }
        }
    }
}
