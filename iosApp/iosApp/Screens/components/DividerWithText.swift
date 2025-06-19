//
//  DividerWithText.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct DividerWithText: View {
    let text: String

    var body: some View {
        HStack {
            Divider().frame(height: 1).background(Color.gray.opacity(0.5))
            Text(text)
                .font(.caption)
                .foregroundColor(.gray)
            Divider().frame(height: 1).background(Color.gray.opacity(0.5))
        }
    }
}
