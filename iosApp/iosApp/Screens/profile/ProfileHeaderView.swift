//
//  ProfileHeaderView.swift
//  iosApp
//
//  Created by gal levi on 15/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct ProfileHeaderView: View {
    @Environment(\.colorScheme) var colorScheme

    let user: User
    let boardsCount: Int

    var body: some View {
        VStack(spacing: 12) {
            // Profile Picture
            if let url = URL(string: user.profilePicture ?? "") {
                AsyncImage(url: url) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: 150, height: 150)
                        .clipShape(Circle())
                } placeholder: {
                    Circle()
                        .fill(Color.gray.opacity(0.3))
                        .frame(width: 150, height: 150)
                        .overlay(ProgressView())
                }
            }

            // Name
            Text(user.name ?? "")
                .font(.system(size: 24, weight: .semibold))
                .foregroundColor(AppColors.textPrimary(for: colorScheme))


            // Stats Row
            HStack {
                StatItem(label: "Rentals", value: "5")
                VerticalDivider()
                StatItem(label: "Boards", value: "\(boardsCount)")
                VerticalDivider()
                StatItem(label: "Spots", value: "10")
            }
            .padding(.vertical, 16)
            .background(AppColors.cardColor(for: colorScheme))
            .clipShape(RoundedRectangle(cornerRadius: 12))

        }
        .frame(maxWidth: .infinity)
    }
}
