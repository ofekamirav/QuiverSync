//
//  ProfileImageUpload.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI

struct ProfileImageUpload: View {
    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            Circle()
                .fill(Color(.secondarySystemBackground))
                .frame(width: 110, height: 110)
                .overlay(
                    Image(systemName: "person.crop.circle.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 90, height: 90)
                        .foregroundColor(AppColors.deepBlue)
                )

            Button(action: {
                // Handle photo upload
            }) {
                Circle()
                    .fill(AppColors.skyBlue)
                    .frame(width: 30, height: 30)
                    .overlay(
                        Image(systemName: "camera.fill")
                            .foregroundColor(.white)
                            .font(.system(size: 16))
                    )
            }
            .offset(x: 4, y: 4)
        }

        Text("Tap to upload profile pic")
            .font(.caption)
            .foregroundColor(.gray)
            .padding(.top, 4)
    }
}
