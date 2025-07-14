//
//  AddSpotScreen.swift
//  iosApp
//
//  Created by gal levi on 09/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared


public struct AddSpotScreen: View {
    
    
    @ObservedObject private(set) var viewModel = AddSpotViewModelWrapper()
    @Binding var showAddSpotScreen : Bool
    @State private var errorMessage: String = ""

    
    
    public var body: some View {
                VStack{
                    switch onEnum(of: viewModel.uiState){
                    case .idle(let data):
                        AddSpotView(
                            addSpotData: data.data,
                            addSpotViewModel: viewModel.viewModel,
                            showAddSpotScreen: $showAddSpotScreen,
                            errorMessage: $errorMessage
                        )
                    case .loading:
                        LoadingView(colorName: "background")
                    case .loaded:
                        LoadingView(colorName: "background")
//                        FavSpotsScreen()
                    case .error(let error): ErrorView(messege: error.message)
                    }
                }
                .onAppear(){
                    viewModel.startObserving()
                }
                .onChange(of: viewModel.uiState) { newState in
                    if let errorState = newState as? AddFavSpotState.Error {
                        errorMessage = errorState.message
                    } else if newState is AddFavSpotState.Loaded {
                        showAddSpotScreen = false
                    }
                }


    }
}
