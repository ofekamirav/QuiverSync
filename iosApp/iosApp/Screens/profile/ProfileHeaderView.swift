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
                        .frame(width: 110, height: 110)
                        .clipShape(Circle())
                } placeholder: {
                    Circle()
                        .fill(Color.gray.opacity(0.3))
                        .frame(width: 110, height: 110)
                        .overlay(ProgressView())
                }
            }

            // Name
            Text(user.name ?? "")
                .font(.system(size: 18, weight: .semibold))
                .foregroundColor(AppColors.deepBlue)


            // Stats Row
            HStack {
                StatItem(label: "Rentals", value: "5")
                VerticalDivider()
                StatItem(label: "Boards", value: "\(boardsCount)")
                VerticalDivider()
                StatItem(label: "Spots", value: "10")
            }
            .padding(.vertical, 16)
            .background(AppColors.foamWhite)
            .clipShape(RoundedRectangle(cornerRadius: 12))

        }
        .frame(maxWidth: .infinity)
    }
}
