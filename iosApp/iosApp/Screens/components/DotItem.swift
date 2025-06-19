//
//  DotItem.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI

struct DotItem: View {
    let text: String

    var body: some View {
        HStack(alignment: .center, spacing: 8) {
            Circle()
                .fill(Color("DeepBlue"))
                .frame(width: 8, height: 8)
            Text(text)
                .font(.footnote)
                .foregroundColor(.gray)
        }
    }
}
