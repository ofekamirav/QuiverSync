//
//  AddBoardScreen.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//




import SwiftUI
import Shared

public struct AddBoardScreen: View {
    
    @ObservedObject private(set) var ViewModel = AddBoardViewModelWrapper()
    
    let onBackRequested: () -> Void

    
    public var body: some View {
        VStack{
            switch onEnum(of: ViewModel.uiState){
            case .idle(let idle):
                AddBoardView(
                    addBoardData : idle.data,
                    onEvent: ViewModel.ViewModel.onEvent,
                    onBackRequested: onBackRequested
                )
            case .loading:
                LoadingView(colorName: "background")
            case .loaded:
                Text("✅ Board added successfully!")
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 1.2) {
                            onBackRequested()
                        }
                    }

            case .error(let error): ErrorView(messege: error.message)
            }
        }
        .onAppear(){
            ViewModel.startObserving()
        }
    }
    
}


