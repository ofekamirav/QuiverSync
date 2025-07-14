//
//  ImagePicker.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import UIKit

struct ImagePicker: UIViewControllerRepresentable {
    enum SourceType {
        case camera, photoLibrary
    }

    @Environment(\.presentationMode) private var presentationMode
    let sourceType: SourceType
    let onImagePicked: (UIImage) -> Void

    func makeUIViewController(context: Context) -> UIImagePickerController {
        print("📸 ImagePicker: makeUIViewController called with sourceType = \(sourceType)")
        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        picker.sourceType = (sourceType == .camera) ? .camera : .photoLibrary
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {
        // No-op, but you can log if needed
        print("🔄 ImagePicker: updateUIViewController called")
    }

    func makeCoordinator() -> Coordinator {
        print("👥 ImagePicker: makeCoordinator called")
        return Coordinator(self)
    }

    class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        let parent: ImagePicker

        init(_ parent: ImagePicker) {
            self.parent = parent
            print("🧩 ImagePicker.Coordinator: initialized")
        }

        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
            print("✅ ImagePicker.Coordinator: didFinishPickingMediaWithInfo called")
            if let image = info[.originalImage] as? UIImage {
                print("🖼 ImagePicker.Coordinator: picked image with size = \(image.size)")
                parent.onImagePicked(image)
            } else {
                print("⚠️ ImagePicker.Coordinator: No image found in info dictionary")
            }
            parent.presentationMode.wrappedValue.dismiss()
            print("🔚 ImagePicker.Coordinator: Dismissing picker after image picked")
        }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            print("❌ ImagePicker.Coordinator: User cancelled image picking")
            parent.presentationMode.wrappedValue.dismiss()
            print("🔚 ImagePicker.Coordinator: Dismissing picker after cancel")
        }
    }
}
