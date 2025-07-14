//
//  AddBoardView.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared

struct AddBoardView: View {
    let addBoardData: AddBoardFormData
    let onEvent: (AddBoardEvent) -> Void
    let onBackRequested: () -> Void

    var body: some View {
        VStack {
            // Steps layout
            if addBoardData.totalSteps <= 1 {
                AddBoardStep1View(data: addBoardData, onEvent: onEvent)
            } else {
                if addBoardData.currentStep == 1 {
                    AddBoardStep1View(data: addBoardData, onEvent: onEvent)
                } else if addBoardData.currentStep == 2 {
                    AddBoardStep2View(data: addBoardData, onEvent: onEvent)
                }
            }

            Spacer()

            WizardNavigationBar(
                currentStep: Int(addBoardData.currentStep),
                totalSteps: Int(addBoardData.totalSteps),
                onBack: {
                    if addBoardData.currentStep > 1 {
                        onEvent(AddBoardEventPreviousStepClicked())
                    } else {
                        onBackRequested()
                    }
                }, onPreviousClicked: { onEvent(AddBoardEventPreviousStepClicked()) },
                onNextClicked: { onEvent(AddBoardEventNextStepClicked()) },
                onFinishClicked: { onEvent(AddBoardEventSubmitClicked()) },
                isTabletLayout: false
            )
        }
        .padding()
    }
}
