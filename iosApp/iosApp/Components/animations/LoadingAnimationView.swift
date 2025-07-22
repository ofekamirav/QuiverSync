//
//  LoadingAnimationView.swift
//  iosApp
//
//  Created by gal levi on 16/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct LoadingAnimationView: View {
    @Environment(\.colorScheme) var colorScheme
    let animationName: String
    let size: CGFloat
    

    var body: some View {
        ZStack{
            LottieView(animationName: animationName, loopMode: .loop, size: size)
//                .frame(width: size, height: size)
                
        }
        .background(AppColors.sectionBackground(for: colorScheme))
    }
}
