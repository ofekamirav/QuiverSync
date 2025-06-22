//
//  ExpendCard.swift
//  iosApp
//
//  Created by gal levi on 21/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

public struct ExpendCardLoading: View {
    @ObservedObject private(set) var viewModel = CombinedForecastQuiverWrapper()
    let favSpot: FavoriteSpot

    public var body: some View {
        VStack {
            switch (onEnum(of: viewModel.forecastState), onEnum(of: viewModel.quiverState)) {
            case (.loading, _), (_, .loading):
                LoadingView(colorName: "foamWhite")
            case (.error(let forecastError), _):
                ErrorView(messege: forecastError.message)
            case (_, .error(let quiverError)):
                ErrorView(messege: quiverError.message)
            case (.loaded(let forecast), .loaded(let quiver)):
                ExpendCard(
                    quiver: quiver.quiver,
                    forecast: forecast.forecast,
                    favSpot: favSpot
                )
                .padding(.horizontal)
            }
        }
        .onAppear {
            viewModel.startObserving()
        }
        .background(AppColors.foamWhite)
    }
}



extension ExpendCardLoading {
    @MainActor
    class CombinedForecastQuiverWrapper: ObservableObject {
        @Published var forecastState: ForecastState
        @Published var quiverState: QuiverState
        
        let forecastVM : ForecastViewModel
        let quiverVM : QuiverViewModel
        
        init() {
            self .forecastVM = ForecastViewModel()
            self .quiverVM = QuiverViewModel()
            
            self.forecastState = forecastVM.uiState.value
            self.quiverState = quiverVM.uiState.value
        }
        
        func startObserving() {
            Task {
                for await state in forecastVM.uiState {
                    self.forecastState = state
                }
            }
            Task {
                for await state in quiverVM.uiState {
                    self.quiverState = state
                }
            }
        }
    }
}


