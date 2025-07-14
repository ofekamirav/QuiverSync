//
//  SurfboardDetailDialog.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct SurfboardDetailDialog: View {
    let board: Surfboard
    let isPresented: Bool
    let onDismiss: () -> Void
    let onDelete: (Surfboard) -> Void

    var body: some View {
        if isPresented {
            ZStack {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture { onDismiss() }

                VStack(spacing: 16) {
                    Text(board.model)
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.blue)

                    AsyncImage(
                        url: URL(string: board.imageRes ?? ""),
                        content: { image in
                            image
                                .resizable()
                                .scaledToFit()
                                .frame(height: 180)
                                .cornerRadius(10)
                        },
                        placeholder: {
                            Image(systemName: "photo")
                                .resizable()
                                .scaledToFit()
                                .frame(height: 180)
                                .foregroundColor(.gray.opacity(0.3))
                        }
                    )

                    Group {
                        detailRow("Company:", board.company)
                        detailRow("Height:", board.height)
                        detailRow("Width:", board.width)
                        detailRow("Volume:", board.volume)
                        detailRow("Added:", board.addedDate)

                        if board.isRentalPublished == true {
                            detailRow("Price / Day:", "\(board.pricePerDay ?? 0.0) $")
                        }
                    }

                    Button(action: {
                        onDelete(board)
                        onDismiss()
                    }) {
                        Text("Delete Surfboard")
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.red)
                            .cornerRadius(12)
                    }

                    Button("Close") {
                        onDismiss()
                    }
                    .foregroundColor(.blue)
                }
                .padding()
                .background(Color.white)
                .cornerRadius(20)
                .padding(32)
            }
        }
    }

    private func detailRow(_ label: String, _ value: String?) -> some View {
        HStack {
            Text(label)
                .fontWeight(.semibold)
            Spacer()
            Text(value ?? "-")
                .foregroundColor(.gray)
        }
    }
}
