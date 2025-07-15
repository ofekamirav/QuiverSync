//
//  UserDetailsSectionView.swift
//  iosApp
//
//  Created by gal levi on 15/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct UserDetailsSectionView: View {
    let user: User

    var body: some View {
        VStack(spacing: 0) {
            UserDetailItem(iconName: "envelope", label: "Email", value: user.email ?? "")
            DividerView()
            UserDetailItem(iconName: "calendar", label: "Date of Birth", value: user.dateOfBirth?.description ?? "-")
            DividerView()
            UserDetailItem(
                iconName: "ruler",
                label: "Height",
                value: formatDouble(user.heightCm, unit: "cm")
            )
            DividerView()
            UserDetailItem(
                iconName: "scalemass",
                label: "Weight",
                value: formatDouble(user.weightKg, unit: "kg")
            )
            DividerView()
            UserDetailItem(iconName: "waveform", label: "Surf Level", value: user.surfLevel ?? "-")
        }
        .padding(.vertical, 8)
        .padding(.horizontal, 0)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.white.opacity(0.8))
        .clipShape(RoundedRectangle(cornerRadius: 12))

    }
}


private func formatDouble(_ value: KotlinDouble?, unit: String = "") -> String {
    if let val = value?.doubleValue {
        return String(format: "%.1f", val) + " \(unit)"
    } else {
        return "-"
    }
}
