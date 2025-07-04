//
//  Untitled 2.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct UserDetailsList: View {
    
    let user: User
    
    var body: some View {
        VStack(spacing: 0) {
            UserDetailRow(icon: "envelope", label: "Email", value: user.email)
            Divider()
            UserDetailRow(icon: "calendar", label: "Date of Birth", value: user.dateOfBirth ?? "Unknown")
            Divider()
            UserDetailRow(icon: "ruler", label: "Height", value: "\(user.heightCm ?? 0) cm")
            Divider()
            UserDetailRow(icon: "scalemass", label: "Weight", value: "\(user.weightKg ?? 0) kg")
            Divider()
            UserDetailRow(icon: "waveform.path.ecg", label: "Surf Level", value: user.surfLevel ?? "Unknown")
        }
        .padding()
        .background(Color.white)
        .cornerRadius(12)
        .shadow(radius: 2)
        .padding(.horizontal)
    }
}

struct UserDetailRow: View {
    let icon: String
    let label: String
    let value: String
    
    var body: some View {
        HStack {
            Image(systemName: icon)
                .foregroundColor(AppColors.deepBlue)
                .frame(width: 24)
            VStack(alignment: .leading) {
                Text(label)
                    .font(.caption)
                    .foregroundColor(.gray)
                    .underline()
                Spacer()
                Text(value)
                    .font(.subheadline)
                    .fontWeight(.medium)
            }
            Spacer()
        }
        .padding(.vertical, 10)
    }
}
