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
    @StateObject private var mainViewModel = MainViewModelWrapper()

    @Environment(\.colorScheme) var colorScheme

    @State private var isLoggedIn: Bool? = nil
    @State private var path = NavigationPath()
    @State private var selectedTab: AppRoute = .home
    @State private var showSplash = true
    @State private var uid: String? = nil
    @State private var isRegistered: Bool = false
    @State private var onBoardingCompleted: Bool? = false
    
    var sessionManager: SessionManager {
            mainViewModel.viewModel.sessionManager
    }
    
    



    var body: some View {
        NavigationStack(path: $path) {
            ZStack {
                AppColors.sectionBackground(for: colorScheme)
                    .ignoresSafeArea()
                
                if showSplash && uid == nil {
                    ZStack {
                        LinearGradient(
                            colors: colorScheme == .dark ? AppColors.loginGradientDark : AppColors.loginGradientLight,
                            startPoint: .top,
                            endPoint: .bottom
                        )
                        .ignoresSafeArea()
                        
                        
                        LottieView(animationName: "splash_intro_animation", loopMode: .playOnce , size : 500)
                    }
                    
                }
                else if uid == nil {
                    if isRegistered {
                        RegisterScreen(
                            onBackBtn: {
                                withAnimation {
                                    isRegistered = false
                                }
                            },
                            isLoggedIn: Binding(get: { isLoggedIn ?? false }, set: { isLoggedIn = $0 }),
                            onLoginSuccess: {
                                withAnimation {
                                        isRegistered = false
                                    }
                                Task {
                                    uid = try? await sessionManager.getUid()
                                    try? await sessionManager.setOnboardingComplete(complete: true)
                                    let result = try? await sessionManager.isOnboardingComplete()
                                    DispatchQueue.main.async {
                                        withAnimation{
                                            onBoardingCompleted = result as? Bool
                                        }
                                    }
                                    print("🔁 onLoginSuccess triggered — this is the uid after login: \(String(describing: uid) )")
                                }
                            }
                            
                        )
                    }
                    else{
                        LoginScreen(
                            onRegisterClick: {
                                withAnimation {
                                    isRegistered = true
                                }
                            },
                            onForgotPasswordClick: { path.append(AppRoute.forgotPassword) },
                            onNavigateToOnboarding: { path.append(AppRoute.completeRegister) },
                            onBackBtn: { path.removeLast(path.count) },
                            isLoggedIn: Binding(
                                get: { isLoggedIn ?? false },
                                set: { isLoggedIn = $0 }
                            ),
                            onLoginSuccess: {
                                Task {
                                    uid = try? await sessionManager.getUid()
                                    print("🔁 onLoginSuccess triggered — this is the uid after login: \(String(describing: uid) )")
                                    
                                }
                            },
                        )
                    }
                } else {
                    if onBoardingCompleted ?? false {
                        OnBoardingScreen(
                            onCompleteClick: {
                                withAnimation {
                                        selectedTab = .home
                                    }
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                        withAnimation {
                                            onBoardingCompleted = false
                                        }
                                    }
                                Task{
                                    try? await sessionManager.setOnboardingComplete(complete: false)
                                    let result = try? await sessionManager.isOnboardingComplete()
                                    DispatchQueue.main.async {
                                        withAnimation{
                                            onBoardingCompleted = result as? Bool
                                        }
                                    }
                                }
                            },
                            isLoggedIn: Binding(get: { isLoggedIn ?? false }, set: { isLoggedIn = $0 }),
                        )
                        .transition(.opacity)
                    }
                    else{
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
                                
                                ExploreScreen()
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
                            .animation(.easeInOut(duration: 0.3), value: selectedTab)
                            
                            
                        }
                    }
                }
            }
            .navigationDestination(for: AppRoute.self) { route in
                switch route {
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
            .background(AppColors.sectionBackground(for: colorScheme))
            .onAppear {
                Task {
                    let sessionManager = SessionManager(context: nil)
                    uid = try? await sessionManager.getUid()
                    onBoardingCompleted = ((try? await sessionManager.isOnboardingComplete() as? Bool) ?? false)
                    isLoggedIn = uid != nil
                }
                print(onBoardingCompleted ?? "Onboarding status not available")
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 4) {
                    showSplash = false
                }
            }
            .onChange(of: uid) { user in
                print("User has logged in: \(String(describing: user))")
            }
            .onChange(of: onBoardingCompleted){ newValue in
                print("Onboarding completed status changed: \(String(describing: onBoardingCompleted))")
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
