//
//  RentalBoardCardView.swift
//  iosApp
//
//  Created by gal levi on 21/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI
import Shared

struct RentalBoardCardView: View {
    @Environment(\.colorScheme) var colorScheme

    let rentalOffer: RentalOffer

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            // Surfboard Image
            AsyncImage(url: URL(string: rentalOffer.surfboardImageUrl ?? "")) { phase in
                switch phase {
                case .success(let image):
                    image
                        .resizable()
                        .scaledToFill()
                        .frame(height: 160)
                        .clipped()
                default:
                    Rectangle()
                        .fill(Color.gray.opacity(0.2))
                        .frame(height: 160)
                        .overlay(
                            Image(systemName: "photo")
                                .font(.largeTitle)
                                .foregroundColor(.gray)
                        )
                }
            }
            .cornerRadius(10)

            VStack(alignment: .leading, spacing: 6) {
                // Surfboard Name
                Text(rentalOffer.surfboardName)
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                    .font(.headline)
                    .lineLimit(1)

                // Owner Info
                HStack(spacing: 8) {
                    AsyncImage(url: URL(string: rentalOffer.ownerProfilePicture ?? "")) { phase in
                        switch phase {
                        case .success(let image):
                            image
                                .resizable()
                                .scaledToFill()
                                .frame(width: 44, height: 44)
                                .clipShape(Circle())
                        default:
                            Circle()
                                .fill(Color.gray.opacity(0.3))
                                .frame(width: 24, height: 24)
                        }
                    }

                    Text(rentalOffer.ownerName)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .lineLimit(1)

                    Spacer()
                }

                Divider()

                // Price
                Text("$\(String(format: "%.0f", rentalOffer.pricePerDay))/day")
                    .font(.subheadline)
                    .fontWeight(.bold)
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
            }
        }
        .padding()
        .background(AppColors.cardColor(for: colorScheme))
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
    }
}
