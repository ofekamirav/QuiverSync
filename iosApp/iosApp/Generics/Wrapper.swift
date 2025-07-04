////
////  Wrapper.swift
////  iosApp
////
////  Created by gal levi on 03/07/2025.
////  Copyright © 2025 orgName. All rights reserved.
////
//
//import Foundation
//import SwiftUI
//import Shared
//
//@MainActor
//class GenericViewModelWrapper<VM: AnyObject, State>: ObservableObject {
//    
//    let viewModel: VM
//    @Published var uistate: State
//    
//    private let stateFlow: Kotlinx_coroutines_coreStateFlow
//
//    init(viewModel: VM, initialState: State, stateFlow: Kotlinx_coroutines_coreStateFlow) {
//        self.viewModel = viewModel
//        self.uistate = initialState
//        self.stateFlow = stateFlow
//    }
//
//    func startObserving() {
//        Task {
//            for await state in stateFlow {
//                self.uistate = state
//            }
//        }
//    }
//}
