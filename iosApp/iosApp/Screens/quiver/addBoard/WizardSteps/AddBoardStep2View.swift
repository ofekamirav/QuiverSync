//
//  AddBoardStep2View.swift
//  iosApp
//
//  Created by gal levi on 25/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct AddBoardStep2View: View {
    @Environment(\.colorScheme) var colorScheme

    let data: AddBoardFormData
    let onEvent: (AddBoardEvent) -> Void

    @State private var showImageOptions = false
    @State private var showCamera = false
    @State private var showGallery = false
    @State private var didStartIOSUpload = false
    @State private var selectedImageData: Data?

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
                    onValueChange: { onEvent(AddBoardEventHeightChanged(value: $0)) },
                    isError: data.heightError != nil,
                    errorMessage: data.heightError
                )
                CustomTextField(
                    text: data.width,
                    label: "Width (In)",
                    keyboardType: .decimalPad,
                    onValueChange: { onEvent(AddBoardEventWidthChanged(value: $0)) },
                    isError: data.widthError != nil,
                    errorMessage: data.widthError
                )
            }

            CustomTextField(
                text: data.volume,
                label: "Volume (L)",
                keyboardType: .decimalPad,
                onValueChange: { onEvent(AddBoardEventVolumeChanged(value: $0)) },
                isError: data.volumeError != nil,
                errorMessage: data.volumeError
            )

            BoardImagePicker(
                imageUrl: data.imageUrl,
                isUploading: data.isUploadingImage,
                onClick: { showImageOptions = true },
                errorMessage: data.imageUploadError,
                frameSize : 250

            )
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
                    selectedImageData = data
                    onEvent(AddBoardEventSurfboardImageSelected(bytes: data.toKotlinByteArray()))
                }
            }
        }
        .sheet(isPresented: $showGallery) {
            ImagePicker(sourceType: .photoLibrary) { image in
                if let data = image.jpegData(compressionQuality: 0.6) {
                    selectedImageData = data
                    onEvent(AddBoardEventSurfboardImageSelected(bytes: data.toKotlinByteArray()))
                }
            }
        }
        .onChange(of: data.uploadFromIOS) { isTriggered in
            if isTriggered && !didStartIOSUpload {
                print("📸 Uploading image to Cloudinary...")
                didStartIOSUpload = true
                Task {
                    do {
                        guard let data = selectedImageData,
                              let image = UIImage(data: data) else {
                            print("❌ Invalid image data — upload skipped")
                            return
                        }

                        let url = try await ImageUploader.uploadImageToCloudinary(image, folder: "surfboards")
                        onEvent(AddBoardEventSurfboardImageIOSChanged(imageURL: url ?? "failed to upload"))
                    } catch {
                        print("❌ iOS image upload failed: \(error)")
                    }
                }
            }
        }
        .background(AppColors.sectionBackground(for: colorScheme))
        .animation(.easeInOut, value: data.heightError)
        .animation(.easeInOut, value: data.widthError)
        .animation(.easeInOut, value: data.volumeError)
    }
}
