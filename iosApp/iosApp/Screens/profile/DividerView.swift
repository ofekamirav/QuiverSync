//
//  DividerView.swift
//  iosApp
//
//  Created by gal levi on 15/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct DividerView: View {
    var body: some View {
        Rectangle()
            .fill(AppColors.borderGray.opacity(0.4))
            .frame(height: 1)
            .padding(.horizontal, 20)
    }
}

