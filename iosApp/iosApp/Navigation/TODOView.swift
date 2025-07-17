//
//  TODOView.swift
//  iosApp
//
//  Created by gal levi on 15/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct TODOView: View {
    let label: String

    var body: some View {
        VStack(spacing: 24) {
            Text("🚧 TODO")
                .font(.largeTitle)
                .foregroundColor(.orange)

            Text("\(label) screen is not implemented yet.")
                .font(.headline)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(.systemGroupedBackground))
        .navigationTitle(label)
    }
}

