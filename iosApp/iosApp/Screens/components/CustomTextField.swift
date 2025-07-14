//
//  CustomTextField.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI

struct CustomTextField: View {
    let text: String
    let label: String
    let keyboardType: UIKeyboardType
    let onValueChange: (String) -> Void
    var isError: Bool = false
    var errorMessage: String? = nil
    var submitLabel: SubmitLabel = .done
    var onSubmit: (() -> Void)? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label)
                .font(.subheadline)
                .foregroundColor(.gray)

            TextField("", text: Binding(
                get: { text },
                set: { onValueChange($0) }
            ))
            .keyboardType(keyboardType)
            .submitLabel(submitLabel)
            .onSubmit {
                onSubmit?()
            }
            .padding()
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(isError ? Color.red : Color.gray.opacity(0.4), lineWidth: 1)
            )

            if isError, let error = errorMessage {
                Text(error)
                    .font(.caption)
                    .foregroundColor(.red)
            }
        }
    }
}

