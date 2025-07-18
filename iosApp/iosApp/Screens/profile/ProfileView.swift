//
//  ProfileView.swift
//  iosApp
//
//  Created by gal levi on 15/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared

struct ProfileView: View {
    @Environment(\.colorScheme) var colorScheme

    let user: User
    let boardsCount: Int
    @Binding var isLoggedIn: Bool

    var body: some View {
        GeometryReader { geometry in
            if geometry.size.width < 600 {
                // COMPACT WIDTH (Mobile)
                ScrollView {
                    VStack(spacing: 16) {
                        ProfileHeaderView(user: user, boardsCount: boardsCount)
                        UserDetailsSectionView(user: user)
                            .frame( maxHeight: .infinity)
                    }
                    .padding(16)
                    .frame(maxWidth: .infinity, alignment: .top)
                }
                .background(AppColors.sectionBackground(for: colorScheme))
            } else {
                // EXPANDED WIDTH (Tablet/Desktop)
                HStack(alignment: .top, spacing: 16) {
                    VStack(spacing: 16) {
                        ProfileHeaderView(user: user, boardsCount: boardsCount)
                    }
                    .frame(maxWidth: .infinity, alignment: .topLeading)

                    VStack {
                        UserDetailsSectionView(user: user)
                    }
                    .frame(maxWidth: .infinity, alignment: .topLeading)
                }
                .padding(16)
                .background(AppColors.sectionBackground(for: colorScheme))
                .frame(maxWidth: .infinity, alignment: .topLeading)
            }
        }
    }
}
