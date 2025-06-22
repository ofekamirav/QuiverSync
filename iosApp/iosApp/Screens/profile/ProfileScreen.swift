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
    
    public var body: some View {
        VStack{
            switch onEnum(of: viewModel.uiState){
            case .loading:
                LoadingView(colorName: "background")
            case .loaded(let loaded):
                ProfileView(user: loaded.user)
            case .error(let error): ErrorView(messege: error.message)
            }
        }
        .onAppear(){
            viewModel.startObserving()
        }
    }
}


//using SKIE for async req , not using corutines
//Get the state from the Kotlin.
extension ProfileScreen{
    
    @MainActor
    class ProfileScreenModelWrapper: ObservableObject {
        let viewModel: UserViewModel
        @Published var uiState: UserState
        
        init() {
            self.viewModel = UserViewModel()
            self.uiState = viewModel.uiState.value
        }
        
        func startObserving() {
            Task{
                for await state in viewModel.uiState{
                    self.uiState = state
                }
            }
        }
    }
}


//#Preview {
//    ProfileScreen()
//}
