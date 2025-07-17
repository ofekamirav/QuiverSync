//
//  ProfileImageUpload.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI

struct ProfileImagePicker: View {
    let imageUrl: String?
    let isUploading: Bool
    let onClick: () -> Void
    let errorMessage: String?

    var body: some View {
        VStack(spacing: 8) {
            ZStack(alignment: .bottomTrailing) {
                if isUploading {
                    Circle()
                        .fill(Color(.secondarySystemBackground))
                        .frame(width: 110, height: 110)
                        .overlay(
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle())
                        )
                } else if let url = imageUrl, let imageURL = URL(string: url) {
                    AsyncImage(url: imageURL, scale: 1) { image in
                        image
                            .resizable()
                            .scaledToFill()
                    } placeholder: {
                        ProgressView()
                    }
                    .frame(width: 110, height: 110)
                    .clipShape(Circle())
                } else {
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
                }

                Button(action: {
                    onClick()
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

            if let error = errorMessage {
                Text(error)
                    .foregroundColor(.red)
                    .font(.caption)
            }
        }
        .frame(maxWidth: .infinity)
        .onTapGesture {
            onClick()
        }
    }
}
