//
//  FinsSetupDropdown.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct FinsSetupDropdownOld: View {
    let selected: FinsSetup
    let onSelected: (FinsSetup) -> Void

    @State private var isExpanded = false

    private let allOptions = FinsSetup.allCases

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("Fin Setup")
                .font(.headline)

            Menu {
                ForEach(allOptions, id: \.self) { option in
                    Button(option.serverName) {
                        onSelected(option)
                    }
                }
            } label: {
                HStack {
                    Text(selected.serverName)
                        .foregroundColor(.primary)
                    Spacer()
                    Image(systemName: "chevron.down")
                        .foregroundColor(.gray)
                }
                .padding()
                .background(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(Color.gray.opacity(0.4), lineWidth: 1)
                )
            }
        }
    }
}
