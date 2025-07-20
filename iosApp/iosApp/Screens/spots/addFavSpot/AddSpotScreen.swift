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
                        LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 300)
                    case .loaded:
                        VStack {
                            Spacer()

                            SuccessMessageView(
                                title: "Spot Added!",
                                subtitle: "Your favorite surf spot is now saved.",
                                systemIconName: "mappin.circle.fill",
                                iconColor: .blue,
                                backgroundColor: .blue,
                                onDismiss: {
                                    showAddSpotScreen = false
                                }
                            )
                            .padding(.top, 80)
                            .transition(.move(edge: .bottom).combined(with: .opacity))
                            .animation(.easeInOut, value: UUID())

                            Spacer()
                        }
                        .onAppear {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                showAddSpotScreen = false
                            }
                            print("Spot added successfully")
                        }

                    case .error(let error):
                        ErrorView(
                        title: "Couldn’t Save That Spot",
                        message: error.message,
                        systemImageName: "mappin.slash",
                        buttonText: "Go Back",
                        onRetry: {
                            showAddSpotScreen = false
                        }
                    )

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
