//
//  AppNavigationView.swift
//  iosApp
//
//  Created by gal levi on 15/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct AppNavigationView: View {
    @Environment(\.colorScheme) var colorScheme

    @State private var isLoggedIn: Bool? = nil
    @State private var path = NavigationPath()
    @State private var selectedTab: AppRoute = .home

    var body: some View {
        NavigationStack(path: $path) {
            ZStack {
                if isLoggedIn == nil {
                    ProgressView("Loading...") // Replace with animation if needed
                } else if isLoggedIn == false {
                    LoginScreen(
                        onRegisterClick: { path.append(AppRoute.register) },
                        onForgotPasswordClick: { path.append(AppRoute.forgotPassword) }, isLoggedIn: Binding(
                            get: { isLoggedIn ?? false },
                            set: { isLoggedIn = $0 }
                        )
                    )
                } else {
                    VStack(spacing: 0) {
                        // Top Bar
                        if showTopBar(for: selectedTab) {
                            HStack {
                                Text(titleForTab(selectedTab))
                                    .font(.title2)
                                    .fontWeight(.semibold)
                                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                                Spacer()
                                if selectedTab == .profile {
                                    Button(action: { path.append(AppRoute.settings) }) {
                                        Image(systemName: "gear")
                                            .foregroundColor(AppColors.textPrimary(for: colorScheme))
                                    }
                                }
                            }
                            .padding()
                            .background(AppColors.sectionBackground(for: colorScheme))
                        }

                        // Main Tabs
                        TabView(selection: $selectedTab) {
                            HomeScreen()
                                .tabItem { Label("Home", systemImage: "house") }
                                .tag(AppRoute.home)

                            FavSpotsScreen()
                                .tabItem { Label("Spots", systemImage: "map") }
                                .tag(AppRoute.spots)

                            Text("TODO: Rentals Screen")
                                .tabItem { Label("Rentals", systemImage: "tray.full") }
                                .tag(AppRoute.rentals)

                            QuiverScreen()
                            .tabItem { Label("Quiver", systemImage: "surfboard.fill") }
                            .tag(AppRoute.quiver)

                            ProfileScreen(isLoggedIn: Binding(get: { isLoggedIn ?? true }, set: { isLoggedIn = $0 }))
                                .tabItem { Label("Profile", systemImage: "person.crop.circle") }
                                .tag(AppRoute.profile)
                        }
                        .background(AppColors.sectionBackground(for: colorScheme))

                    }
                }
            }
            .navigationDestination(for: AppRoute.self) { route in
                switch route {
                case .register:
                    RegisterScreen(
                        onBackBtn: { path.removeLast(path.count) },
                        onSuccess: { path.append(AppRoute.completeRegister) },
                        isLoggedIn: Binding(get: { isLoggedIn ?? false }, set: { isLoggedIn = $0 })
                    )


                case .settings:
                    SettingsScreen(
                        onEditProfile: { path.append(AppRoute.editProfile) },
                        onSecuritySettings: { path.append(AppRoute.securityAndPrivacy) },
                        onNotificationsSettings: {},
                        onHelpSupport: {},
                        onLogout: {
                            isLoggedIn = false
                            path.removeLast(path.count)
                        }
                    )


                case .editProfile:
                    EditProfileScreen(
                        isLoggedIn: Binding(get: { isLoggedIn ?? false }, set: { isLoggedIn = $0 })
                    )

                case .securityAndPrivacy:
                    SecurityAndPrivacyScreen()

                case .forgotPassword:
                    ForgotPasswordScreen(
                        isLoggedIn: Binding(get: { isLoggedIn ?? false }, set: { isLoggedIn = $0 })
                    )

                case .completeRegister:
                    OnBoardingScreen(
                        onCompleteClick: {
                            path.removeLast(path.count) // reset stack
                            selectedTab = .home
                        },
                        isLoggedIn: Binding(get: { isLoggedIn ?? false }, set: { isLoggedIn = $0 })
                    )



                case .addSurfboard:
                    AddBoardScreen(
                        onBackRequested: {
                            path.removeLast()
                        }
                    )

                default:
                    Text("TODO: Screen not implemented for \(route)")
                }
            }
            .onAppear {
                Task {
                    let sessionManager = SessionManager(context: nil)
                    let uid = try? await sessionManager.getUid()
                    isLoggedIn = uid != nil
                }
            }
        }
    }

    // Helper: Which tabs show a top bar
    func showTopBar(for tab: AppRoute) -> Bool {
        return ![.login, .register, .completeRegister, .forgotPassword].contains(tab)
    }

    // Helper: Tab titles
    func titleForTab(_ tab: AppRoute) -> String {
        switch tab {
        case .home: return "QuiverSync"
        case .spots: return "Surf Spots"
        case .rentals: return "Rentals"
        case .quiver: return "My Quiver"
        case .profile: return "Profile"
        default: return ""
        }
    }
}
