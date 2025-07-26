//
//  QuiverScreen.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI
import Shared

public struct QuiverScreen: View {
    @ObservedObject private(set) var ViewModel = QuiverViewModelWrapper()
    @State private var selectedBoard: Surfboard? = nil
    @State private var boardToDelete: Surfboard? = nil
    @State private var errorMessage: String = ""
    
    public var body: some View {
        VStack{
            switch onEnum(of: ViewModel.uiState){
            case .loading:
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 300)
            case .loaded(let loaded):
                QuiverView(
                        boards: loaded.boards,
                        boardToPublish: ViewModel.boardToPublishLatest,
                        boardViewModel : ViewModel.ViewModel,
                        onAddClick: {
                        print("hello")
                        },
                        selectedBoard: $selectedBoard,
                        boardToDelete: $boardToDelete
                    )
            case .error(let error):
                ErrorView(
                    title: "Your Quiver Got Washed Away ",
                    message: error.message,
                    systemImageName: "shippingbox.fill",
                    buttonText: "Retry",
                    onRetry: {
//                        ViewModel.ViewModel.refreshQuiver()
                        print("Retrying quiver fetch...")
                    }
                )

            }
        }
        .onAppear(){
            ViewModel.startObserving()
        }
    }
    
}


