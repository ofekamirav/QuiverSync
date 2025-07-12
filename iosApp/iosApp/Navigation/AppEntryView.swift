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
                    MainTabView(isLoggedIn : $isLoggedIn)
                } else {
                    LoginScreen(
                        onRegisterClick: { path.append(AppRoute.register) },
                        isLoggedIn: $isLoggedIn,
//                        onSignInClick: {
//                            isLoggedIn = true
//                            path.removeLast(path.count) // Clear to main
//                        }
                       
                    )
                }
            }
            .onAppear{
                Task{
                    do{
                        let sessionManager = SessionManager(context: nil)
                        let uid = try await sessionManager.getUid()
                        if uid != nil {
                            isLoggedIn = true
                        }
                    }
                }
            }
            .navigationDestination(for: AppRoute.self) { route in
                switch route {
                case .register:
                    RegisterScreen(
                        onBackBtn: {
                            path.removeLast(path.count) // Clear to main
                        },
                        onSuccess: {
                                isLoggedIn = true
                        },
                        isLoggedIn: $isLoggedIn,
                    )
                case .main:
                    MainTabView(isLoggedIn : $isLoggedIn)
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
