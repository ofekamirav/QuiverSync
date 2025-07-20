//
//  AddBoardScreen.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//




import SwiftUI
import Shared

public struct AddBoardScreen: View {
    
    @ObservedObject private(set) var ViewModel = AddBoardViewModelWrapper()
    
    let onBackRequested: () -> Void

    
    public var body: some View {
        VStack{
            switch onEnum(of: ViewModel.uiState){
            case .idle(let idle):
                AddBoardView(
                    addBoardData : idle.data,
                    onEvent: ViewModel.ViewModel.onEvent,
                    onBackRequested: onBackRequested
                )
            case .loading:
                LoadingAnimationView(animationName: "quiver_sync_loading_animation", size: 300)
            case .loaded:
                VStack {
                    Spacer()

                    SuccessMessageView(
                        title: "Board Added!",
                        subtitle: "Your surfboard has been added to your quiver.",
                        systemIconName: "surfboard.fill", // Or fallback: "checkmark.circle.fill"
                        iconColor: .blue,
                        backgroundColor: .blue,
                        onDismiss: {
                            onBackRequested()
                        }
                    )
                    .padding(.top, 80)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                    .animation(.easeInOut, value: UUID())

                    Spacer()
                }
                .onAppear {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        onBackRequested()
                    }
                }


            case .error(let error):
                ErrorView(
                    title: "Couldn’t Add Your Board 🛹",
                    message: error.message,
                    systemImageName: "xmark.octagon.fill",
                    buttonText: "Go Back",
                    onRetry: {
                        onBackRequested()
                    }
                )

            }
        }
        .onAppear(){
            ViewModel.startObserving()
        }
    }
    
}


