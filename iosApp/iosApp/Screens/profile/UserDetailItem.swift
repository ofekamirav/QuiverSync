//
//  UserDetailItem.swift
//  iosApp
//
//  Created by gal levi on 15/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct UserDetailItem: View {
    let iconName: String
    let label: String
    let value: String

    var body: some View {
        HStack(alignment: .center, spacing: 12) {
            Image(systemName: iconName)
                .foregroundColor(AppColors.deepBlue)
                .frame(width: 20, height: 20)

            VStack(alignment: .leading, spacing: 2) {
                Text(label)
                    .font(.system(size: 13))
                    .foregroundColor(.gray)

                Text(value)
                    .font(
                        label == "Surf Level"
                        ? .system(size: 16, weight: .bold)
                        : .system(size: 16, weight: .medium)
                    )
                    .foregroundColor(AppColors.textDark)
            }

            Spacer()
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 14)
    }
}
