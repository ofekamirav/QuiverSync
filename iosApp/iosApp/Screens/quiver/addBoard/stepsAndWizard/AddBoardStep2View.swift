//
//  AddBoardStep2View.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared
import Foundation


struct AddBoardStep2View: View {
    let data: AddBoardFormData
    let onEvent: (AddBoardEvent) -> Void
    
    @State private var showImageOptions = false
    @State private var showCamera = false
    @State private var showGallery = false

    var body: some View {
        VStack(spacing: 20) {
            Text("Details")
                .font(.title2)
                .frame(maxWidth: .infinity, alignment: .leading)

            HStack(spacing: 16) {
                CustomTextField(
                    text: data.height,
                    label: "Height (In)",
                    keyboardType: .decimalPad,
                    onValueChange: { onEvent(AddBoardEventHeightChanged(value: $0)) }
                )
                CustomTextField(
                    text: data.width,
                    label: "Width (In)",
                    keyboardType: .decimalPad,
                    onValueChange: { onEvent(AddBoardEventWidthChanged(value: $0)) }
                )
            }

            CustomTextField(
                text: data.volume,
                label: "Volume (L)",
                keyboardType: .decimalPad,
                onValueChange: { onEvent(AddBoardEventVolumeChanged(value: $0)) }
            )

            BoardImagePicker(
                imageUrl: data.imageUrl,
                isUploading: data.isUploadingImage,
                onClick: { showImageOptions = true },
                errorMessage: data.imageUploadError
            )
            .frame(height: 160)

            Spacer()
        }
        .padding()
        .sheet(isPresented: $showImageOptions) {
            ImageSourceSelectorSheet(
                onDismiss: { showImageOptions = false },
                onTakePhoto: {
                    showImageOptions = false
                    showCamera = true
                },
                onChooseFromGallery: {
                    showImageOptions = false
                    showGallery = true
                }
            )
        }
        .sheet(isPresented: $showCamera) {
            ImagePicker(sourceType: .camera) { image in
                if let data = image.jpegData(compressionQuality: 0.6) {
                    onEvent(AddBoardEventSurfboardImageSelected(bytes: data.toKotlinByteArray()))
                }
            }
        }
        .sheet(isPresented: $showGallery) {
            ImagePicker(sourceType: .photoLibrary) { image in
                if let data = image.jpegData(compressionQuality: 0.6) {
                    onEvent(AddBoardEventSurfboardImageSelected(bytes: data.toKotlinByteArray()))
                }
            }
        }

    }
}
