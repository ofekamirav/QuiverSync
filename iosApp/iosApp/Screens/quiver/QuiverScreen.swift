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
    
    @ObservedObject private(set) var viewModel = QuiverViewModelWrapper()
    
    public var body: some View {
        VStack{
            switch onEnum(of: viewModel.uiState){
            case .loading:
                Text("Loading...")
            case .success:
                Text("Success")
            case .error:
                Text("Failure")
            
                
            }
        }
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


#Preview {
    QuiverScreen()
}
