//
//  AddBoardStep1View.swift
//  iosApp
//
//  Created by gal levi on 25/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct AddBoardStep1View: View {
    @Environment(\.colorScheme) var colorScheme

    let data: AddBoardFormData
    let onEvent: (AddBoardEvent) -> Void
    
    var body: some View {
        ScrollView{
            VStack(alignment: .leading, spacing: 16) {
                AddBoardInputField(
                    title: "Model Name",
                    text: data.model,
                    errorText: data.modelError,
                    onTextChanged: { onEvent(AddBoardEventModelChanged(value: $0)) }
                )
                
                AddBoardInputField(
                    title: "Company / Shaper",
                    text: data.company,
                    errorText: data.companyError,
                    onTextChanged: { onEvent(AddBoardEventCompanyChanged(value: $0)) }
                )
                
                Text("Select Fins Setup")
                    .font(.headline)
                
                FinsSetupPicker(
                    selected: data.finSetup,
                    onSelected: { onEvent(AddBoardEventFinsSetupChanged(value: $0)) }
                )
                
                Text("Select Board Type")
                    .font(.headline)
                
                SurfboardTypeGrid(
                    selectedType: data.boardType,
                    onSelected: { onEvent(AddBoardEventBoardTypeChanged(value: $0)) }
                )
            }
            .background(AppColors.sectionBackground(for: colorScheme))
            .padding()

        }
    }
}
