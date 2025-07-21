//
//  OnBoardingView.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct OnBoardingView: View {
    let form: OnboardingFormData
    let onEvent: (OnboardingEvent) -> Void

    @Environment(\.colorScheme) var colorScheme

    @State private var name = ""
    @State private var height = ""
    @State private var weight = ""
    @State private var phoneNumber = ""
    @State private var selectedSurfLevel: SurfLevel? = nil
    @State private var showImageOptions = false
    @State private var showCamera = false
    @State private var showGallery = false
    @State private var localImageUrl: String? = nil
    @State private var didStartIOSUpload = false
    @State private var selectedImageData: Data?

    var body: some View {
        ZStack {
            ScrollView {
                VStack(spacing: 20) {
                    // Header
                    Text("Personalize Your Experience")
                        .font(.title)
                        .fontWeight(.semibold)
                        .padding(.top)
                        .foregroundColor(AppColors.textPrimary(for: colorScheme))


                    Text("This helps us find the best spots and gear for you.")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                        .multilineTextAlignment(.center)
                        .foregroundColor(AppColors.textPrimary(for: colorScheme))


                    // Date Picker (custom view or native)
                    CustomDatePickerView(
                        title: "Date of Birth",
                        selectedDate: form.dateOfBirth,
                        onDateSelected: { onEvent(OnboardingEventDateOfBirthChanged(value: $0)) },
                        errorMessage: form.dateOfBirthError
                    )

                    // Height
                    TextField("Height (cm)", text: $height)
                        .onChange(of: height) { newValue in
                            onEvent(OnboardingEventHeightChanged(value: newValue))
                        }
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                    .keyboardType(.numberPad)
                    .padding()
                    .background(AppColors.cardColor(for: colorScheme))
                    .cornerRadius(10)

                    if let error = form.heightError {
                        Text(error).font(.caption).foregroundColor(.red).frame(maxWidth: .infinity, alignment: .leading)
                    }

                    // Weight
                    TextField("Weight (kg)", text: $weight)
                        .onChange(of: weight) { newValue in
                            onEvent(OnboardingEventWeightChanged(value: newValue))
                        }
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                    .keyboardType(.numberPad)
                    .padding()
                    .background(AppColors.cardColor(for: colorScheme))
                    .cornerRadius(10)

                    if let error = form.weightError {
                        Text(error).font(.caption).foregroundColor(.red).frame(maxWidth: .infinity, alignment: .leading)
                    }
                    
                    // Phone number
                    TextField("Phone number", text: $phoneNumber)
                        .onChange(of: phoneNumber) { newValue in
                            onEvent(OnboardingEventPhoneNumberChanged(value: newValue))
                        }
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                    .keyboardType(.numberPad)
                    .padding()
                    .background(AppColors.cardColor(for: colorScheme))
                    .cornerRadius(10)

                    if let error = form.phoneNumberError {
                        Text(error).font(.caption).foregroundColor(.red).frame(maxWidth: .infinity, alignment: .leading)
                    }

                    // Surf Level
                    Text("How do you rate your surfing?")
                        .font(.headline)
                        .foregroundColor(AppColors.textPrimary(for: colorScheme))

                    SurfLevelSelectorView(
                        selectedLevel: selectedSurfLevel,
                        onSelect: {
                            selectedSurfLevel = $0
                            onEvent(OnboardingEventSurfLevelChanged(level: $0))
                        },
                        error: form.surfLevelError
                    )

                    // Profile Image Picker
                    ProfileImagePicker(
                        imageUrl: form.profileImageUrl,
                        isUploading: didStartIOSUpload,
                        onClick: { showImageOptions = true },
                        errorMessage: form.profileImageError
                    )
                    .frame(height: 180)
                    
                    HStack(alignment: .center, spacing: 12) {
                        Button(action: {
                            onEvent(OnboardingEventOnAgreementChange(isAgreed: !form.agreedToTerms))
                        }) {
                            Image(systemName: form.agreedToTerms ? "checkmark.square.fill" : "square")
                                .resizable()
                                .frame(width: 24, height: 24)
                                .foregroundColor(form.agreedToTerms ? AppColors.surfBlue : .gray)
                        }
                        .buttonStyle(PlainButtonStyle())

                        Text("I agree to the Terms and Conditions")
                            .font(.subheadline)
                            .foregroundColor(AppColors.textPrimary(for: colorScheme))
                            .onTapGesture {
                                onEvent(OnboardingEventOnAgreementChange(isAgreed: !form.agreedToTerms))
                            }

                        Spacer()
                    }
                    .padding(.vertical, 8)

                    if let error = form.termsError {
                        Text(error)
                            .font(.caption)
                            .foregroundColor(.red)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }


                    
                    GradientButton(text: "Continue"){
                        onEvent(OnboardingEventContinueClicked())
                    }
                    .buttonStyle(PressableButtonStyle())



                }
                .padding()
            }
            .onAppear {
                height = form.heightCm
                weight = form.weightKg
                selectedSurfLevel = form.selectedSurfLevel
                localImageUrl = form.profileImageUrl
                phoneNumber = form.phoneNumber
            }
            .background(AppColors.sectionBackground(for: colorScheme))
            .gesture(
                TapGesture().onEnded {
                    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                }
            )
            // Image Option Sheet
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

            // Camera Picker
            .sheet(isPresented: $showCamera) {
                ImagePicker(sourceType: .camera) { image in
                    if let data = image.jpegData(compressionQuality: 0.6) {
                        selectedImageData = data
                        onEvent(OnboardingEventProfileImageSelected(bytes: data.toKotlinByteArray()))
                    }
                }
            }

            // Gallery Picker
            .sheet(isPresented: $showGallery) {
                ImagePicker(sourceType: .photoLibrary) { image in
                    if let data = image.jpegData(compressionQuality: 0.6) {
                        selectedImageData = data
                        onEvent(OnboardingEventProfileImageSelected(bytes: data.toKotlinByteArray()))
                    }
                }
            }

            // iOS uploader
            .onChange(of: form.uploadFromIOS) { triggered in
                print("🔄 iOS upload triggered: \(didStartIOSUpload)")
                print("🔄 selectedImageData: \(form.uploadFromIOS)")
                if triggered && !didStartIOSUpload {
                    didStartIOSUpload = true
                    Task {
                        do {
                            guard let data = selectedImageData,
                                  let image = UIImage(data: data) else {
                                print("❌ Invalid image data")
                                return
                            }
 
                            let url = try await ImageUploader.uploadImageToCloudinary(image, folder: "profiles")
                            onEvent(OnboardingEventProfileImageIOSChanged(imageURL: url ?? "failed to upload"))
                        } catch {
                            print("❌ iOS image upload failed: \(error)")
                            onEvent(OnboardingEventProfileImageIOSChanged(imageURL: "failed to upload"))
                        }
                        print("🔄 selectedImageData: \(form.uploadFromIOS)")
                        DispatchQueue.main.async {
                            didStartIOSUpload = false
                        }
                    }
                }
            }
            .onChange(of: didStartIOSUpload) { value in
                print("🔄 didStartIOSUpload changed: \(didStartIOSUpload)")
            
            }

            // Loading overlay
            if didStartIOSUpload {
                Color.black.opacity(0.4).ignoresSafeArea()
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 180)
            }
        }
    }
}


struct PressableButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? 0.97 : 1.0)
            .opacity(configuration.isPressed ? 0.85 : 1.0)
            .animation(.easeInOut(duration: 0.15), value: configuration.isPressed)
    }
}
