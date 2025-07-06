//
//  ForecastResults.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

public struct ForecastResults: View {
    
    @ObservedObject private(set) var viewModel = FavSpotsViewModelWrapper()
    
    
    public var body: some View {
        VStack{
            switch onEnum(of: viewModel.uiState){
            case .loading:
                LoadingView(colorName: "background")
            case .loaded(let loaded):
                FavSpotsView(favSpots: loaded.spots)
            case .error(let error): ErrorView(messege: error.message)
            }
        }
        .onAppear(){
            viewModel.startObserving()
        }
    }
}



extension ForecastResults {
    @MainActor
    class FavSpotsViewModelWrapper : ObservableObject{
        let viewModel: FavSpotsViewModel
        @Published var uiState: FavSpotsState
        
        init(){
            self.viewModel = KoinKt.favSpotsViewModel()
            self.uiState = viewModel.uiState.value
        }
        
        func startObserving(){
            Task{
                for await state in viewModel.uiState{
                    self.uiState = state
                }
            }
        }
    }
}
