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
                LoadingView(colorName: "background")
            case .loaded(let loaded):
                HomeView(data:loaded.homePageData)
            case .error(let error):
                ErrorView(messege: error.message)
                
            }
        }
        .onAppear{
            viewModel.startObserving()
        }
    }
}
