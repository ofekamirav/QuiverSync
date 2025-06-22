//
//  LoadingView.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared


struct LoadingView: View {
    let colorName: String

    var body: some View {
        ZStack {
            AppColors.color(from: colorName)
                .ignoresSafeArea()
            ProgressView()
        }
    }
}


extension AppColors {
    static func color(from name: String) -> Color {
        switch name.lowercased() {
        case "foamwhite":
            return AppColors.foamWhite
        case "deepblue":
            return AppColors.deepBlue
        case "surfblue":
            return AppColors.surfBlue
        case "background":
            return AppColors.background
        default:
            return AppColors.background // fallback color
        }
    }
}
