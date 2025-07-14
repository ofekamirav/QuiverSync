//
//  BoardImagePicker.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI

struct BoardImagePicker: View {
    let imageUrl: String?
    let isUploading: Bool
    let onClick: () -> Void
    let errorMessage: String?
    var modifier: some ViewModifier = EmptyModifier()

    var body: some View {
        VStack(spacing: 8) {
            ZStack {
                if isUploading {
                    ProgressView()
                        .frame(width: 120, height: 120)
                } else if let url = imageUrl, let imageURL = URL(string: url) {
                    AsyncImage(url: imageURL) { image in
                        image
                            .resizable()
                            .scaledToFill()
                    } placeholder: {
                        ProgressView()
                    }
                    .frame(width: 120, height: 120)
                    .clipShape(RoundedRectangle(cornerRadius: 16))
                } else {
                    RoundedRectangle(cornerRadius: 16)
                        .stroke(style: StrokeStyle(lineWidth: 1, dash: [5]))
                        .frame(width: 120, height: 120)
                        .overlay(
                            Image(systemName: "plus")
                                .foregroundColor(.gray)
                        )
                }
            }
            .onTapGesture {
                onClick()
            }

            if let error = errorMessage {
                Text(error)
                    .foregroundColor(.red)
                    .font(.caption)
            }
        }
        .frame(maxWidth: .infinity)
    }
}
