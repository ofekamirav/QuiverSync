////
////  SurfboardCard.swift
////  iosApp
////
////  Created by gal levi on 20/06/2025.
////  Copyright © 2025 orgName. All rights reserved.
////
//
//import SwiftUI
//import Shared
//
//
//struct SurfboardCard: View {
//    let board: Surfboard
//    let onClick: () -> Void
//    
//    var body: some View {
//        VStack{
//            Image("\(board.imageRes)")
//                .resizable()
//                .frame(width: 128, height: 138)
//            Text("\(board.model)")
//                .font(.subheadline )
//                .foregroundColor(AppColors.deepBlue)
//                .padding()
//            HStack{
//                Text("For Rent")
//                    .font(.subheadline )
//                Toggle("For Rent", isOn: .constant(board.isRentalPublished))
//                            .font(.subheadline )
//                            .tint(AppColors.deepBlue)
//                            .labelsHidden()
//                            .toggleStyle(SwitchToggleStyle(tint: AppColors.sandOrange))
//            }
//        }
//        .padding()
//        .background(.white)
//        .cornerRadius(16)
//        .shadow(radius: 4)
//        .overlay(
//            RoundedRectangle(cornerRadius: 16)
//                .stroke(board.isRentalPublished ? AppColors.sandOrange : AppColors.background, lineWidth: 1)
//        )
//        .onTapGesture {
//            onClick()
//        }
//    }
//}
