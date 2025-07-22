//
//  HomeScreen.swift
//  iosApp
//
//  Created by gal levi on 14/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared
import Foundation

struct HomeScreen: View {
    @ObservedObject private(set) var viewModel = HomeViewModelWrapper()
    var body: some View{
        VStack{
            switch onEnum(of: viewModel.uistate) {
            case .loading:
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 300)
            case .loaded(let loaded):
                HomeView(data:loaded.homePageData)
            case .error(let error):
                ErrorView(
                    title: "Tide’s Out on the Home Feed",
                    message: error.message,
                    systemImageName: "house.slash",
                    buttonText: "Retry",
                    onRetry: {
                        viewModel.retryHomeScreen()
                    }
                )


                
            }
        }
        .onAppear{
            viewModel.startObserving()
        }
    }
}
