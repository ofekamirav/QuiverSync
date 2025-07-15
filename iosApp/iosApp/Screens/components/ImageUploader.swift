//
//  ImageUploader.swift
//  iosApp
//
//  Created by gal levi on 15/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Foundation
import Shared

struct ImageUploader {
    static func uploadImageToCloudinary(_ image: UIImage, folder: String) async throws -> String? {
        print("📤 [ImageUploader] Starting upload to Cloudinary in folder: \(folder)")

        guard let imageData = image.jpegData(compressionQuality: 0.25) else {
            print("❌ [ImageUploader] Failed to convert image to JPEG data")
            return nil
        }

        let cloudName = "dku8ltduu"
        let uploadPreset = "quiverSync"
        let urlString = "https://api.cloudinary.com/v1_1/\(cloudName)/image/upload"
        print("🌐 [ImageUploader] Upload URL: \(urlString)")

        guard let url = URL(string: urlString) else {
            print("❌ [ImageUploader] Invalid URL")
            return nil
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"

        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")

        var body = Data()
        let filename = UUID().uuidString + ".jpg"
        let fieldName = "file"

        print("📦 [ImageUploader] Preparing multipart form-data")
        print("🖼️ Filename: \(filename)")
        print("📁 Folder: \(folder)")
        print("📋 Upload preset: \(uploadPreset)")

        // Image file
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"\(fieldName)\"; filename=\"\(filename)\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: image/jpeg\r\n\r\n".data(using: .utf8)!)
        body.append(imageData)
        body.append("\r\n".data(using: .utf8)!)

        // Upload preset
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n".data(using: .utf8)!)
        body.append("\(uploadPreset)\r\n".data(using: .utf8)!)

        // Folder field
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"folder\"\r\n\r\n".data(using: .utf8)!)
        body.append("\(folder)\r\n".data(using: .utf8)!)

        // Close
        body.append("--\(boundary)--\r\n".data(using: .utf8)!)

        request.httpBody = body

        print("🚀 [ImageUploader] Sending upload request...")

        let (data, response) = try await URLSession.shared.data(for: request)

        if let httpResponse = response as? HTTPURLResponse {
            print("✅ [ImageUploader] HTTP Status Code: \(httpResponse.statusCode)")
        }

        let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
        print("📨 [ImageUploader] Cloudinary Response: \(json ?? [:])")

        if let url = json?["secure_url"] as? String {
            print("✅ [ImageUploader] Uploaded successfully. URL: \(url)")
            return url
        } else {
            print("❌ [ImageUploader] Failed to retrieve secure_url from response")
            return nil
        }
    }
}
