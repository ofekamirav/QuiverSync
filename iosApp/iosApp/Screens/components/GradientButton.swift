//
//  GradientButton.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI

struct GradientButton: View {
    @Environment(\.colorScheme) var colorScheme
    var btnColor: [Color] {
        colorScheme == .dark ? AppColors.loginGradientDark : AppColors.loginGradientLight
    }

    let text: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(text)
                .foregroundColor(.white)
                .bold()
                .padding()
                .frame(maxWidth: .infinity)
                .background(
                    LinearGradient(
                        colors: btnColor,
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                )
                .cornerRadius(16)
        }
    }
}
