//
//  WizardNavigationBar.swift
//  iosApp
//
//  Created by gal levi on 25/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct WizardNavigationBar: View {
    @Environment(\.colorScheme) var colorScheme
    let currentStep: Int
    let totalSteps: Int
    let onPreviousClicked: () -> Void
    let onNextClicked: () -> Void
    let onFinishClicked: () -> Void
    let onBack: () -> Void

    var body: some View {
        HStack {
            if currentStep > 1 {
                Button(action: onPreviousClicked) {
                    Label("Back", systemImage: "arrow.left")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
            } else {
                Button(action: onBack) {
                    Label("Back", systemImage: "arrow.backward")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
            }

            Button(
                action: currentStep < totalSteps ? onNextClicked : onFinishClicked
            ) {
                Text(currentStep < totalSteps ? "Next" : "Finish")
                    .bold()
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
        }
        .background(AppColors.sectionBackground(for: colorScheme))
        .padding()
        
    }
}
