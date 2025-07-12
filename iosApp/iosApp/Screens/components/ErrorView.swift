//
//  ErrorView.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared


struct ErrorView: View {
    var messege : String
    var body: some View {
        ZStack{
            AppColors.background
                .ignoresSafeArea()
            Text(messege)
                .font(.title)
            Text("Please try again later")
                
        }
    }
}
