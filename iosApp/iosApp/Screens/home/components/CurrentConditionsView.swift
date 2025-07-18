//
//  CurrentConditionsView.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct CurrentConditionsView: View {
    @Environment(\.colorScheme) var colorScheme

    let waveHeight: String
    let wind: String
    let swellPeriod: String

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(spacing:12){
                Text("Current Conditions")
                    .font(.headline)
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                Spacer()
            }
            

            HStack(spacing: 32) {
                ConditionItemView(label: "Wave Height", value: waveHeight, icon: "water.waves")
                ConditionItemView(label: "Wind", value: wind, icon: "wind")
                ConditionItemView(label: "Swell Period", value: swellPeriod, icon: "clock.arrow.circlepath")
            }
            .frame(maxWidth: .infinity, alignment: .center)
        }
        .padding()
        .frame(maxWidth: .infinity)
        .background(AppColors.cardColor(for: colorScheme))
        .cornerRadius(20)
    }
}
