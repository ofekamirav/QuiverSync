//
//  SocialLoginButton.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct SocialLoginButton: View {
    let text: String
    let imageName: String


    var body: some View {
        Button(action: {
            // Add action
        }) {
            HStack {
                Image(systemName: imageName)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 24, height: 24)
                    .foregroundColor(.gray)

                Text(text)
                    .font(.footnote)
                    .foregroundColor(.primary)
            }
            .padding(10)
            .background(Color(.secondarySystemBackground))
            .cornerRadius(12)
        }
    }
}
