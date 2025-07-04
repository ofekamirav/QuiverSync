//
//  AppEntryView.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared
import Foundation

struct AppEntryView: View {
    @State private var path = NavigationPath()
    @State private var isLoggedIn = false
    
    var body: some View {
        NavigationStack(path: $path) {
            Group {
                if isLoggedIn {
                    MainTabView()
                } else {
                    LoginScreen(
                        onRegisterClick: { path.append(AppRoute.register) },
//                        onSignInClick: {
//                            isLoggedIn = true
//                            path.removeLast(path.count) // Clear to main
//                        }
                       
                    )
                }
            }
            .navigationDestination(for: AppRoute.self) { route in
                switch route {
                case .register:
                    RegisterScreen(
//                        onReturnToMain : {
//                            path.removeLast(path.count) // Clear to main
//                        }
                        onBackBtn: {
//                            isLoggedIn = true
                            path.removeLast(path.count) // Clear to main
                        },
                        onSuccess: {
                                isLoggedIn = true
                        },
                    )
                case .main:
                    MainTabView()
                case .welcomeSheet:
                    WelcomeBottomSheetScreen(
                        onDismiss: { path = .init() },
                        onGetStarted: { path.append(AppRoute.main) }
                    )
                }
            }
        }
    }
    
    
    
    enum AppRoute: Hashable {
        case register
        case main
        case welcomeSheet
    }
}
