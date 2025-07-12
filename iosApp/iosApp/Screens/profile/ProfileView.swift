//
//  ProfileView.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared

public struct ProfileView: View {
    
    let user : User
    @Binding var isLoggedIn: Bool
    

    
    public var body: some View {
        ScrollView{
            VStack(spacing: 20){
                
                ProfileHeader(user: user)
                
                StatRow()
                
                UserDetailsList(user: user)
                
                Spacer()
                
                ProfileActions(onLogout: {
                    Task {
                        let sessionManager = SessionManager(context: nil)
                        try await sessionManager.clearUserData()
                        isLoggedIn = false
                    }
                }, onEdit: {
                    print( "Edit pressed")
                })
                Spacer(minLength: 10)
                
            }
            .background(AppColors.background)
        }
        .background(AppColors.background)
    }
}




