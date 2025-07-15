//
//  AddBoardStep1View.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI
import Shared

struct AddBoardStep1View: View {
    let data: AddBoardFormData
    let onEvent: (AddBoardEvent) -> Void
    
    

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            CustomTextField(
                text: data.model,
                label: "Model Name",
                keyboardType: .default,
                onValueChange: { onEvent(AddBoardEventModelChanged(value: $0)) },
                isError: data.modelError != nil,
                errorMessage: data.modelError
            )

            CustomTextField(
                text: data.company,
                label: "Company / Shaper",
                keyboardType: .default,
                onValueChange: { onEvent(AddBoardEventCompanyChanged(value: $0)) },
                isError: data.companyError != nil,
                errorMessage: data.companyError
            )

            Text("Select fins")
                .font(.headline)

            FinsSetupDropdown(
                selected: data.finSetup,
                onSelected: { onEvent(AddBoardEventFinsSetupChanged(value: $0)) }
            )

            Text("Select surfboard type")
                .font(.headline)

            SelectableBoardTypeGrid(
                selectedType: data.boardType,
                onTypeSelected: { onEvent(AddBoardEventBoardTypeChanged(value: $0)) }
            )
        }
        .padding()
        .animation(.easeInOut, value: data.modelError)
        .animation(.easeInOut, value: data.companyError)
    }
}
