//
//  WelcomeBottomSheetView.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI

struct WelcomeBottomSheetScreen: View {
    let onDismiss: () -> Void
    let onGetStarted: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Image("bottom_sheet_image")
                .resizable()
                .scaledToFill()
                .frame(height: 160)
                .clipped()
                .cornerRadius(16)

            Text("Welcome to QuiverSync!")
                .font(.title2)
                .fontWeight(.bold)
                .foregroundColor(Color("DeepBlue"))

            VStack(alignment: .leading, spacing: 8) {
                DotItem(text: "Match surfboards to wave forecasts")
                DotItem(text: "Check real-time surf conditions")
                DotItem(text: "Rent or share boards with the community")
            }
            .padding(.horizontal, 12)

            Button(action: onGetStarted) {
                Text("Get Started")
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color("SkyBlue"))
                    .cornerRadius(16)
            }

            Spacer().frame(height: 8)
        }
        .padding()
        .presentationDetents([.medium])
        .presentationDragIndicator(.visible)
    }
}
