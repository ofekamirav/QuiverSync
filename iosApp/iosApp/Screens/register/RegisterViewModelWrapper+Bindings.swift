////
////  RegisterViewModelWrapper+Bindings.swift
////  iosApp
////
////  Created by gal levi on 02/07/2025.
////  Copyright © 2025 orgName. All rights reserved.
////
//
//
//extension RegisterScreen.RegisterViewModelWrapper {
//    func onNameChanged(_ value: String) {
//        viewModel.onEvent(event: RegisterEventNameChanged(value: value))
//    }
//
//    func onEmailChanged(_ value: String) {
//        viewModel.onEvent(event: Register)
//    }
//
//    func onPasswordChanged(_ value: String) {
//        viewModel.onEvent(event: RegisterEventPasswordChanged(value: value))
//    }
//
//    func onSignUpClicked() {
//        viewModel.onEvent(event: RegisterEventSignUpClicked())
//    }
//
//    var name: String {
//        if let loaded = uistate as? RegisterState.Loaded {
//            return loaded.data.name
//        }
//        return ""
//    }
//
//    var email: String {
//        if let loaded = uistate as? RegisterState.Loaded {
//            return loaded.data.email
//        }
//        return ""
//    }
//
//    var password: String {
//        if let loaded = uistate as? RegisterState.Loaded {
//            return loaded.data.password
//        }
//        return ""
//    }
//}
