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
    let rentalOffer: BoardForDisplay

    var body: some View {
        let surfboard = rentalOffer.board

        VStack(alignment: .leading, spacing: 10) {
            HStack(alignment: .top, spacing: 12) {
                // 🔹 Surfboard Image (matches height)
                GeometryReader { geo in
                    AsyncImage(url: URL(string: surfboard?.surfboardPic ?? "")) { phase in
                        switch phase {
                        case .success(let image):
                            image
                                .resizable()
                                .scaledToFill()
                                .frame(width: 110, height: geo.size.height)
                                .clipped()
                                .cornerRadius(16)
                        default:
                            Rectangle()
                                .fill(Color.gray.opacity(0.2))
                                .frame(width: 110, height: geo.size.height)
                                .cornerRadius(16)
                        }
                    }
                }
                .frame(width: 110)

                // 🔸 Details Section
                VStack(alignment: .leading, spacing: 6) {
                    // Name + Price
                    HStack(alignment: .top) {
                        Text(surfboard?.model ?? "No Model")
                            .font(.title3)
                            .fontWeight(.bold)
                            .foregroundColor(AppColors.deepBlue)

                        Spacer()

                        Text("$\(String(format: "%.0f", surfboard?.pricePerDay ?? 0))/day")
                            .font(.subheadline)
                            .fontWeight(.bold)
                            .foregroundColor(AppColors.valueSand)
                    }

                    Divider()

                    // Specs Grid
                    Grid(horizontalSpacing: 16, verticalSpacing: 8) {
                        GridRow {
                            SpecItem(label: "Type", value: surfboard?.type ?? "")
                            SpecItem(label: "Height", value: surfboard?.height ?? "")
                        }
                        GridRow {
                            SpecItem(label: "Width", value: surfboard?.width ?? "")
                            SpecItem(label: "Volume", value: surfboard?.volume ?? "")
                        }
                        GridRow{
                            HStack(spacing: 8) {
                                AsyncImage(url: URL(string: surfboard?.ownerPic ?? "")) { phase in
                                    switch phase {
                                    case .success(let image):
                                        image
                                            .resizable()
                                            .scaledToFill()
                                            .frame(width: 38, height: 38)
                                            .clipShape(Circle())
                                    default:
                                        Circle()
                                            .fill(Color.gray.opacity(0.3))
                                            .frame(width: 38, height: 38)
                                    }
                                }

                                Text(surfboard?.ownerName ?? "Unknown")
                                    .font(.footnote)
                                    .foregroundColor(.secondary)

                                Spacer()
                            }
                            .padding(.top, 8)

                        }
                        .gridCellColumns(2)
                        
                    }
                    .padding(.top, 4)
                }
            }
        }
        .padding()
        .background(AppColors.cardColor(for: colorScheme))
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
    }
}



// MARK: - Reusable Spec Item View
struct SpecItem: View {
    let label: String
    let value: String

    var body: some View {
        VStack(spacing: 2) {
            Text(label)
                .font(.caption)
                .foregroundColor(.secondary)
            Text(value)
                .font(.subheadline)
                .foregroundColor(.primary)
        }
        .frame(minWidth: 50)
    }
}


//
//#Preview {
//    RentalBoardCardView(
//        rentalOffer: BoardForDisplay(
//            board: BoardForRent(
//                surfboardId: "1",
//                ownerName: "Gal Levi",
//                ownerPic: "https://via.placeholder.com/150",
//                ownerPhoneNumber: "123-456-7890",
//                surfboardPic: "https://via.placeholder.com/300",
//                model: "SharpEye Inferno",
//                type: "Shortboard",
//                height: "5'11\"",
//                width: "18.5\"",
//                volume: "25.5L",
//                pricePerDay: 50.0
//            ), id: "1"
//        )
//    )
//}
