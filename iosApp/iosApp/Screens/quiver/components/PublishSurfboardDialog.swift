//
//  PublishSurfboardDialog.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct PublishSurfboardDialog: View {
    let surfboard: Surfboard
    let isPresented: Bool
    let onConfirm: (Double) -> Void
    let onDismiss: () -> Void

    @State private var pricePerDay: String = ""
    @State private var errorMessage: String? = nil

    var body: some View {
        if isPresented {
            ZStack {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture { onDismiss() }

                VStack(spacing: 16) {
                    Text("Publish \(surfboard.model) for Rental")
                        .font(.title2)
                        .fontWeight(.bold)
                        .multilineTextAlignment(.center)
                        .foregroundColor(.blue)

                    Text("Set a daily price for your surfboard rental offer.")
                        .font(.subheadline)
                        .multilineTextAlignment(.center)
                        .foregroundColor(.secondary)

                    TextField("Price per Day ($)", text: $pricePerDay)
                        .keyboardType(.decimalPad)
                        .padding()
                        .background(Color.gray.opacity(0.1))
                        .cornerRadius(10)

                    if let error = errorMessage {
                        Text(error)
                            .font(.caption)
                            .foregroundColor(.red)
                    }

                    Button(action: {
                        let parsed = Double(pricePerDay)
                        if let parsed = parsed, parsed > 0 {
                            errorMessage = nil
                            onConfirm(parsed)
                        } else {
                            errorMessage = "Please enter a valid positive number"
                        }
                    }) {
                        Text("Publish")
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.orange)
                            .cornerRadius(12)
                    }

                    Button("Cancel") {
                        onDismiss()
                    }
                    .foregroundColor(.blue)
                }
                .padding()
                .background(Color.white)
                .cornerRadius(20)
                .padding(32)
                .onAppear {
                    pricePerDay = surfboard.pricePerDay?.description ?? ""
                }
            }
        }
    }
}
