//
//  MainViewModelWrapper.swift
//  iosApp
//
//  Created by gal levi on 26/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared
import Foundation

class MainViewModelWrapper: ObservableObject {
    let viewModel: MainViewModel = KoinKt.mainViewModel()

//    init() {
//        viewModel = MainViewModel(
//            sessionManager: SessionManager(context: nil),
//            startSyncsUseCase: KoinKt.startSyncsUseCase()
//        )
//    }
}
