//
//  CustomInputField.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//




import SwiftUI

struct CustomInputField: View {
    let label: String
    @Binding var text: String
    let systemImage: String
    var isSecure: Bool = false

    var body: some View {
        HStack {
            Image(systemName: systemImage)
                .foregroundColor(.gray)

            if isSecure {
                SecureField(label, text: $text)
                    .textContentType(.password)
            } else {
                TextField(label, text: $text)
                    .autocapitalization(.none)
                    .keyboardType(.emailAddress)
            }
        }
        .padding()
        .background(Color(.secondarySystemBackground))
        .cornerRadius(12)
    }
}
