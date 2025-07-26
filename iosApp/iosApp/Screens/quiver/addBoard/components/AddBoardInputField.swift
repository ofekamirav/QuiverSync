//
//  AddBoardInputField.swift
//  iosApp
//
//  Created by gal levi on 25/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import UIKit
import SwiftUICore
import SwiftUI

struct AddBoardInputField: View {
    let title: String
    let text: String
    var keyboardType: UIKeyboardType = .default
    var errorText: String? = nil
    let onTextChanged: (String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            TextField(title, text: Binding(
                get: { text },
                set: { onTextChanged($0) }
            ))
            .padding()
            .background(Color(.secondarySystemBackground))
            .cornerRadius(10)
            .keyboardType(keyboardType)

            if let error = errorText {
                Text(error)
                    .font(.caption)
                    .foregroundColor(.red)
            }
        }
    }
}
