//
//  Theme.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI


enum AppColors {
    // MARK: - Light Theme
    static let deepBlue = Color(#colorLiteral(red: 0.1302595735, green: 0.3887715936, blue: 0.4519690871, alpha: 1)) // #026873
    static let surfBlue = Color(#colorLiteral(red: 0.302, green: 0.714, blue: 0.675, alpha: 1)) // #4db6ac
    static let skyBlue = Color(#colorLiteral(red: 0.655, green: 0.871, blue: 0.835, alpha: 1)) // #a7ded5
    static let foamWhite = Color(#colorLiteral(red: 0.937, green: 0.976, blue: 0.973, alpha: 1)) // #eff9f8
    static let background = Color(#colorLiteral(red: 0.961, green: 0.961, blue: 0.961, alpha: 1)) // #f5f5f5
    static let sandOrange = Color(#colorLiteral(red: 1, green: 0.655, blue: 0.149, alpha: 1)) // #ffa726
    static let textDark = Color(#colorLiteral(red: 0.149, green: 0.196, blue: 0.22, alpha: 1)) // #263238
    static let borderGray = Color(#colorLiteral(red: 0.878, green: 0.878, blue: 0.878, alpha: 1)) // #e0e0e0
    static let error = Color(#colorLiteral(red: 0.827, green: 0.361, blue: 0.361, alpha: 1)) // #d35c5c
    // MARK: - Gradients
    static let loginGradientLight = [skyBlue, surfBlue]
    static let loginGradientDark = [deepBlue, surfBlue]

    // MARK: - Dark Theme
    static let darkBackground = Color(#colorLiteral(red: 0.071, green: 0.071, blue: 0.071, alpha: 1)) // #121212
    static let darkSurface = Color(#colorLiteral(red: 0.118, green: 0.118, blue: 0.118, alpha: 1)) // #1e1e1e
    static let darkText = Color(#colorLiteral(red: 0.925, green: 0.925, blue: 0.925, alpha: 1)) // #ececec
    static let darkCard = Color(#colorLiteral(red: 0.165, green: 0.165, blue: 0.165, alpha: 1)) // #2a2a2a
    static let darkFoamWhite = Color(#colorLiteral(red: 0.69, green: 0.816, blue: 0.8, alpha: 1)) // #b0d0cc
    static let darkBorder = Color(#colorLiteral(red: 0.267, green: 0.267, blue: 0.267, alpha: 1)) // #444444
    static let darkSky = Color(#colorLiteral(red: 0.502, green: 0.796, blue: 0.769, alpha: 1)) // #80cbc4
}




extension AppColors {
    static func textPrimary(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkFoamWhite : textDark
    }

    static func cardColor(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkCard : foamWhite
    }

    static func sectionBackground(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkBackground : background
    }

    static func chipText(for scheme: ColorScheme) -> Color {
        scheme == .dark ? foamWhite : deepBlue
    }

    static func chipBackground(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkSky.opacity(0.2) : skyBlue.opacity(0.1)
    }
    
    static func divider(for scheme: ColorScheme) -> Color {
        scheme == .dark ? darkBorder.opacity(0.5) : borderGray.opacity(0.4)
    }

}
