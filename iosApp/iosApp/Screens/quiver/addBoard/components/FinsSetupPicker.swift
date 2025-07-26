//
//  FinsSetupPicker.swift
//  iosApp
//
//  Created by gal levi on 25/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct FinsSetupPicker: View {
    let selected: FinsSetup
    let onSelected: (FinsSetup) -> Void

    var body: some View {
        Menu {
            ForEach(FinsSetup.allCases, id: \.self) { setup in
                Button(action: {
                    onSelected(setup)
                }) {
                    Label(
                        title: { Text(setup.name.capitalized) },
                        icon: {
                            if setup == selected {
                                Image(systemName: "checkmark")
                            }
                        }
                    )
                }
            }
        } label: {
            HStack {
                Text(selected.name.capitalized)
                    .fontWeight(.medium)
                Spacer()
                Image(systemName: "chevron.down")
            }
            .padding()
            .frame(maxWidth: .infinity)
            .background(Color.gray.opacity(0.1))
            .cornerRadius(8)
        }
    }
}
