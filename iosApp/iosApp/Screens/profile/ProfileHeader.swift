//
//  ProfileHeader.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared

public struct ProfileHeader: View {
    let user: User
    
    public var body: some View {
        VStack{
            ZStack(alignment: .bottomTrailing){
                Image("\(user.imageUrl)")
                    .resizable()
                    .frame(width: 120, height: 120)
                    .background(AppColors.borderGray)
                    .clipShape(Circle())
                Button(action:{
                    print("Edit photo tapped")
                }) {
                    Image(systemName: "pencil.circle")
                        .foregroundColor(AppColors.foamWhite)
                        .padding(8)
                        .background(AppColors.deepBlue)
                        .clipShape(Circle())
                }
            }
            
            Text("\(user.name)")
                .font(.title3)
                .fontWeight(.bold)
                .foregroundColor(AppColors.deepBlue)
        }

    }
}
