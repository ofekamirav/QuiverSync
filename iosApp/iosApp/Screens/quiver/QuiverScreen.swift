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
    @State private var selectedBoard: Surfboard?=nil
    
    public var body: some View {
        VStack{
            switch onEnum(of: ViewModel.uiState){
            case .loading:
                LoadingView()
            case .loaded(let loaded):
                QuiverView(quiver: loaded.quiver, onBoardTap: {board in
                    selectedBoard = board
                })
            case .error(let error): ErrorView(messege: error.message)
            }
        }
        .onAppear(){
            ViewModel.startObserving()
        }
    }
    
}

struct LoadingView: View {
    var body: some View {
        ProgressView()
            .background(AppColors.background)
    }
}

struct ErrorView: View {
    var messege : String
    var body: some View {
        Text(messege)
            .font(.title)
    }
}

//using SKIE for async req , not using corutines
//Get the state from the Kotlin.
extension QuiverScreen {
    @MainActor
    class QuiverViewModelWrapper: ObservableObject {
        let ViewModel: QuiverViewModel
        @Published var uiState: QuiverState
        
        init() {
            self.ViewModel = QuiverViewModel()
            self.uiState = ViewModel.uiState.value
        }
        
        func startObserving() {
            Task{
                for await state in ViewModel.uiState{
                    self.uiState = state
                }
            }
        }
    }
}


//#Preview {
//    QuiverScreen()
//}
