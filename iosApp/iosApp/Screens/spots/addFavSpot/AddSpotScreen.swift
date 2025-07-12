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
    
    
    public var body: some View {
                VStack{
                    switch onEnum(of: viewModel.uiState){
                    case .idle(let data):
                
                        AddSpotView(
                            addSpotData: data.data,
                            addSpotViewModel: viewModel.viewModel,
                            showAddSpotScreen: $showAddSpotScreen
                        )
                    case .loading:
                        LoadingView(colorName: "background")
                    case .loaded(let loaded):
                        FavSpotsScreen()
                    case .error(let error): ErrorView(messege: error.message)
                    }
                }
                .onAppear(){
                    viewModel.startObserving()
                }
    }
}
