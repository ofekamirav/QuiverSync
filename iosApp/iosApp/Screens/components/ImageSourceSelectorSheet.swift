//
//  ImageSourceSelectorSheet.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct ImageSourceSelectorSheet: View {
    let onDismiss: () -> Void
    let onTakePhoto: () -> Void
    let onChooseFromGallery: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Button(action: {
                onTakePhoto()
            }) {
                Text("📷 Take Photo")
                    .frame(maxWidth: .infinity)
                    .padding()
            }

            Button(action: {
                onChooseFromGallery()
            }) {
                Text("🖼️ Choose from Gallery")
                    .frame(maxWidth: .infinity)
                    .padding()
            }

            Button(action: {
                onDismiss()
            }) {
                Text("Cancel")
                    .foregroundColor(.red)
                    .frame(maxWidth: .infinity)
                    .padding()
            }
        }
        .padding()
        .background(.ultraThinMaterial)
        .cornerRadius(16)
        .padding()
    }
}
