//
//  MainTabView.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct MainTabView: View {
    @State private var selectedTab: BottomTab = .home
    @Binding var isLoggedIn: Bool
    
    
    
    var body: some View {
        VStack(spacing: 0) {
            // Top Bar
            HStack {
                Text(selectedTab.rawValue.localizedCapitalized)
                    .font(.title2)
                    .fontWeight(.semibold)
                    .foregroundColor(AppColors.deepBlue)
                    .padding(.horizontal)
                Spacer()
            }
            .frame(height: 44)
            .background(AppColors.background)
            .shadow(color: Color.black.opacity(0.05), radius: 4, y: 1)
            
            
            TabView(selection: $selectedTab) {
                HomeScreen()
                    .tabItem { Label("Home", systemImage: "house") }
                    .tag(BottomTab.home)
                QuiverScreen()
                    .tabItem { Label("Quiver", systemImage: "surfboard.fill") }
                    .tag(BottomTab.quiver)
                ProfileScreen(isLoggedIn: $isLoggedIn)
                    .tabItem { Label("Profile", systemImage: "person.crop.circle") }
                    .tag(BottomTab.profile)
                
                FavSpotsScreen()
                    .tabItem { Label("Forecast", systemImage: "sun.min") }
                    .tag(BottomTab.forecast)
    //
    //            RentalsHubScreen()
    //                .tabItem { Label("Rentals", systemImage: "car") }
    //                .tag(BottomTab.rentals)
    //
    //            QuiverScreen(viewModel: /* inject KMP viewmodel */)
    //                .tabItem { Label("Quiver", systemImage: "surfboard.fill") }
    //                .tag(BottomTab.quiver)
    //
    //            ProfileScreen(
    //                user: UserProfile( /* your mock or ViewModel bridge */ ),
    //                onLogout: { /* logout logic */ }
    //            )
    //            .tabItem { Label("Profile", systemImage: "person") }
    //            .tag(BottomTab.profile)
            }
        }

        
//        Text("Hello World!")
    }
}

//#Preview {
//    MainTabView()
//}
