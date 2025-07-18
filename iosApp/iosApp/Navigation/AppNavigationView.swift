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
    @State private var showSplash = true
    @State private var uid: String? = nil



    var body: some View {
        NavigationStack(path: $path) {
            ZStack {
                if showSplash && uid == nil {
                    ZStack {
                        LinearGradient(
                            colors: colorScheme == .dark ? AppColors.loginGradientDark : AppColors.loginGradientLight,
                            startPoint: .top,
                            endPoint: .bottom
                        )
                        .ignoresSafeArea()

                        LottieView(animationName: "splash_intro_animation", loopMode: .playOnce , size : 500)
                            .frame(width: 200, height: 200)
                    }

                }
                else if uid == nil {
                    LoginScreen(
                        onRegisterClick: { path.append(AppRoute.register) },
                        onForgotPasswordClick: { path.append(AppRoute.forgotPassword) },
                        isLoggedIn: Binding(
                            get: { isLoggedIn ?? false },
                            set: { isLoggedIn = $0 }
                        ),
                        onLoginSuccess: {
                                Task {
                                    let sessionManager = SessionManager(context: nil)
                                    uid = try? await sessionManager.getUid()
                                    print("🔁 onLoginSuccess triggered — this is the uid after login: \(String(describing: uid) )")

                                }
                            },
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
                        isLoggedIn: Binding(get: { isLoggedIn ?? false }, set: { isLoggedIn = $0 }),
                        onLoginSuccess: {
                                Task {
                                    let sessionManager = SessionManager(context: nil)
                                    uid = try? await sessionManager.getUid()
                                    print("🔁 onLoginSuccess triggered — this is the uid after login: \(String(describing: uid) )")

                                }
                            }
                    )


                case .settings:
                    SettingsScreen(
                        onEditProfile: { path.append(AppRoute.editProfile) },
                        onSecuritySettings: { path.append(AppRoute.securityAndPrivacy) },
                        onNotificationsSettings: {},
                        onHelpSupport: {},
                        onLogout: {
                            print("🔁 onLogout triggered — resetting navigation and state")
                            path.removeLast(path.count)
                            isLoggedIn = false
                            uid = nil
                        }

                    )


                case .editProfile:
                    EditProfileScreen(
                        onSucsess: {
                            selectedTab = .profile
                            path.removeLast(path.count)
                        }
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
                    uid = try? await sessionManager.getUid()
                    isLoggedIn = uid != nil
                }
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 4) {
                        showSplash = false
                }
            }
            .onChange(of: uid) { user in
                print("User has logged in: \(String(describing: user))")
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
