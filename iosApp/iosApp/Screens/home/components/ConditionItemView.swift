//
//  ConditionItemView.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI

struct ConditionItemView: View {
    let label: String
    let value: String
    let icon: String

    var body: some View {
        VStack {
            Image(systemName: icon)
                .foregroundColor(AppColors.skyBlue)
                .font(.title2)

            Text(label)
                .font(.caption)
                .foregroundColor(.gray)

            Text(value)
                .fontWeight(.bold)
                .foregroundColor(AppColors.deepBlue)
        }
    }
}
