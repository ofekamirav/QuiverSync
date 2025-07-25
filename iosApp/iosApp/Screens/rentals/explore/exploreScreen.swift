//
//  exploreScreen.swift
//  iosApp
//
//  Created by gal levi on 21/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared


public struct ExploreScreen: View {
    
    @ObservedObject private(set) var viewModel = ExploreScreenModelWrapper()
    
    public var body: some View {
        
        VStack{
            switch onEnum(of: viewModel.uiState){
            case .loading:
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 300)
            case .loaded(let data):
                ExploreView(
                    exploreData: data.communityBoards
                )
            case .error(let error):
                ErrorView(
                    title: "No Rental Boards Available",
                    message: error.message,
                    systemImageName: "surfboard.fill",
                    buttonText: "Try Again",
                    onRetry: {
//                        viewModel.viewModel.refreshBoards()
                        print("Retry tapped, but no refresh method available")
                    }
                )

            }
        }
        .onAppear(){
            viewModel.startObserving()
        }
    }
}
