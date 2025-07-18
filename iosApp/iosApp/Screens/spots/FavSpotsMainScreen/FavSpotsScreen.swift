//
//  ForecastResults.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

public struct FavSpotsScreen: View {
    
    @ObservedObject private(set) var viewModel = FavSpotsViewModelWrapper()
    
    
    public var body: some View {
        VStack{
            switch onEnum(of: viewModel.uiState){
            case .loading:
                LoadingView(colorName: "background")
            case .loaded(let loaded):
                FavSpotsView(
                    favSpots: loaded.favSpotsData,
                    favSpotsViewModel: viewModel.viewModel,
                )
            case .error(let error):
                ErrorView(
                    title: "Can’t Load Your Spots 📍",
                    message: error.message,
                    systemImageName: "map.fill",
                    buttonText: "Retry",
                    onRetry: {
                        viewModel.startObserving()
                    }
                )
            }
        }
        .onAppear(){
            viewModel.startObserving()
        }
    }
}




