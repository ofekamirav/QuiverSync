//
//  ProfileActions.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI
import Shared


struct ProfileActions: View {
    let onLogout: () -> Void
    let onEdit: () -> Void
    
    var body: some View {
        HStack(spacing: 16) {
            Button(action: onLogout) {
                HStack {
                    Image(systemName: "rectangle.portrait.and.arrow.forward")
                    Text("Log Out")
                }
                .padding()
                .frame(maxWidth: .infinity)
                .background(AppColors.sandOrange)
                .foregroundColor(.white)
                .cornerRadius(12)
            }
            
            Button(action: onEdit) {
                HStack {
                    Image(systemName: "pencil")
                    Text("Edit")
                }
                .padding()
                .frame(maxWidth: .infinity)
                .background(AppColors.deepBlue.opacity(0.8))
                .foregroundColor(.white)
                .cornerRadius(12)
            }
        }
        .padding(.horizontal)
    }
}

