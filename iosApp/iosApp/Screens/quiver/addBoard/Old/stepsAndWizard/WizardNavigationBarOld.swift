////
////  WizardNavigationBar.swift
////  iosApp
////
////  Created by gal levi on 13/07/2025.
////  Copyright © 2025 orgName. All rights reserved.
////
//
//import SwiftUI
//
//struct WizardNavigationBarOld: View {
//    @Environment(\.colorScheme) var colorScheme
//
//    let currentStep: Int
//    let totalSteps: Int
//    let onBack: () -> Void
//    let onPreviousClicked: () -> Void
//    let onNextClicked: () -> Void
//    let onFinishClicked: () -> Void
//    let isTabletLayout: Bool
//
//    var body: some View {
//        HStack {
//            if currentStep > 1 {
//                OutlinedButton(
//                    text: "Back",
//                    icon: "chevron.left",
//                    isEnabled: true,
//                    fullWidth: false,
//                    action: onPreviousClicked
//                )
//            } else {
//                OutlinedButton(
//                    text: "Back",
//                    icon: "chevron.left",
//                    isEnabled: true,
//                    fullWidth: false,
//                    action: onBack
//                )
//            }
//
//            Spacer()
//
//            OutlinedButton(
//                text: currentStep < totalSteps ? "Next" : "Finish",
//                icon: currentStep < totalSteps ? "arrow.right" : "checkmark",
//                isEnabled: true,
//                fullWidth: false,
//                action: {
//                    if currentStep < totalSteps {
//                        onNextClicked()
//                    } else {
//                        onFinishClicked()
//                    }
//                }
//            )
//        }
//        .padding(.horizontal, 16)
//        .padding(.vertical, 12)
//        .background(AppColors.sectionBackground(for: colorScheme))
//        .shadow(radius: 4)
//    }
//}
