//
//  EditProfileFormView.swift
//  iosApp
//
//  Created by gal levi on 16/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct EditProfileFormView: View {
    @Environment(\.colorScheme) var colorScheme

    let form: EditUserFormData
    let onEvent: (EditUserDetailsEvent) -> Void
    @Binding var showToast: Bool
    @Binding var navigateToProfile: Bool
    let loading: Bool

    @State private var showImageOptions = false
    @State private var showCamera = false
    @State private var showGallery = false
    @State private var didStartIOSUpload = false
    @State private var selectedImageData: Data?

    @State private var name: String = ""
    @State private var height: String = ""
    @State private var weight: String = ""

    var body: some View {
        ZStack {
            VStack(spacing: 16) {
                // Name
                // Inside VStack(spacing: 16) {
                Group {
                    // Name
                    TextField("Name", text: $name)
                        .onChange(of: name) { onEvent(EditUserDetailsEventOnNameChange(name: $0)) }
                        .padding()
                        .background(AppColors.cardColor(for: colorScheme))
                        .cornerRadius(10)

                    if let error = form.nameError {
                        Text(error)
                            .font(.caption)
                            .foregroundColor(.red)
                            .padding(.leading, 4)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }

                    // Height
                    TextField("Height (cm)", text: $height)
                        .keyboardType(.decimalPad)
                        .onChange(of: height) { newValue in
                            if let value = Double(newValue) {
                                onEvent(EditUserDetailsEventOnHeightChange(height: value))
                            }
                        }
                        .padding()
                        .background(AppColors.cardColor(for: colorScheme))
                        .cornerRadius(10)

                    if let error = form.heightError {
                        Text(error)
                            .font(.caption)
                            .foregroundColor(.red)
                            .padding(.leading, 4)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }

                    // Weight
                    TextField("Weight (kg)", text: $weight)
                        .keyboardType(.decimalPad)
                        .onChange(of: weight) { newValue in
                            if let value = Double(newValue) {
                                onEvent(EditUserDetailsEventOnWeightChange(weight: value))
                            }
                        }
                        .padding()
                        .background(AppColors.cardColor(for: colorScheme))
                        .cornerRadius(10)

                    if let error = form.weightError {
                        Text(error)
                            .font(.caption)
                            .foregroundColor(.red)
                            .padding(.leading, 4)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                }


                Spacer()

                BoardImagePicker(
                    imageUrl: form.profilePicture,
                    isUploading: form.isUploadingImage,
                    onClick: { showImageOptions = true },
                    errorMessage: form.imageUploadError
                )
                .frame(height: 200)

                Spacer()

                Button(action: {
                    onEvent(EditUserDetailsEventOnSubmit())
                }) {
                    Text("Save")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(AppColors.surfBlue)
                        .foregroundColor(.white)
                        .cornerRadius(12)
                }
            }
            .padding()
            .onAppear {
                name = form.name ?? ""
                height = form.height?.description ?? ""
                weight = form.weight?.description ?? ""
            }
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
                        onEvent(EditUserDetailsEventProfileImageSelected(bytes: data.toKotlinByteArray()))
                    }
                }
            }
            .sheet(isPresented: $showGallery) {
                ImagePicker(sourceType: .photoLibrary) { image in
                    if let data = image.jpegData(compressionQuality: 0.6) {
                        selectedImageData = data
                        onEvent(EditUserDetailsEventProfileImageSelected(bytes: data.toKotlinByteArray()))
                    }
                }
            }
            .onChange(of: form.uploadFromIOS) { isTriggered in
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
                            onEvent(EditUserDetailsEventProfileImageIOSChanged(imageURL: url ?? "failed to upload"))
                        } catch {
                            print("❌ iOS image upload failed: \(error)")
                        }
                        DispatchQueue.main.async {
                                didStartIOSUpload = false
                        }
                    }
                }
            }

            // ✅ Lottie loading overlay
            if form.isUploadingImage {
                Color.black.opacity(0.5).ignoresSafeArea()
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 200)
            }
            
            if loading{
                Color.black.opacity(0.5).ignoresSafeArea()
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 300)
            }

            // ✅ Toast overlay
            if showToast {
                VStack {
                    Spacer()
                    Text("Profile updated successfully")
                        .padding()
                        .background(Color.black.opacity(0.8))
                        .foregroundColor(.white)
                        .cornerRadius(12)
                        .transition(.opacity)
                        .padding(.bottom, 40)
                }
                .zIndex(1)
            }
        }
    }
}
