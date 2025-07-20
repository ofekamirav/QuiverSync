//
//  ProfileScreen.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

public struct ProfileScreen: View {
    @ObservedObject private(set) var viewModel = ProfileScreenModelWrapper()
    @Binding var isLoggedIn: Bool
    
    public var body: some View {
        VStack{
            switch onEnum(of: viewModel.uiState){
            case .loading:
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 300)
            case .loaded(let loaded):
                ProfileView(user: loaded.user,
                            boardsCount: Int(loaded.boards),
                            isLoggedIn: $isLoggedIn)
            case .error(let error):
                ErrorView(
                    title: "Couldn’t Load Your Profile 🧢",
                    message: error.message,
                    systemImageName: "person.crop.circle.badge.exclamationmark",
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




