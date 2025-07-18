//
//  DividerView.swift
//  iosApp
//
//  Created by gal levi on 15/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct DividerView: View {
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        Rectangle()
            .fill(colorScheme == .dark ? AppColors.darkBorder.opacity(0.5) : AppColors.borderGray.opacity(0.4))
            .frame(height: 1)
            .padding(.horizontal, 20)
    }
}

