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
    

    
    public var body: some View {
        ScrollView{
            VStack(spacing: 20){
                
                ProfileHeader(user: user)
                
                StatRow()
                
                UserDetailsList(user: user)
                
                Spacer()
                
                ProfileActions(onLogout: {
                    print("Logout pressed")
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




