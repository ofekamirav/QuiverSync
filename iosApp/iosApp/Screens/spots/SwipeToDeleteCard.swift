//
//  SwipeToDeleteCard.swift
//  iosApp
//
//  Created by gal levi on 20/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct SwipeToDeleteCard<Content: View>: View {
    @Environment(\.colorScheme) var colorScheme
    let content: () -> Content
    let onDelete: () -> Void

    @State private var offsetX: CGFloat = 0
    @GestureState private var dragOffset: CGFloat = 0

    private let swipeThreshold: CGFloat = -80

    var body: some View {
        ZStack(alignment: .trailing) {
            // 🔴 Delete background
            HStack {
                Spacer()
                Button(action: {
                    withAnimation {
                        onDelete()
                    }
                }) {
                    Image(systemName: "trash")
                        .foregroundColor(.white)
                        .padding()
                        .background(Color.red)
                        .clipShape(Circle())
                }
                .padding(.trailing, 16)
            }

            // 🟦 Foreground content
            content()
                .background(AppColors.sectionBackground(for: colorScheme))
                .offset(x: offsetX + dragOffset)
                .gesture(
                    DragGesture()
                        .updating($dragOffset) { value, state, _ in
                            if value.translation.width < 0 {
                                state = value.translation.width
                            }
                        }
                        .onEnded { value in
                            withAnimation(.easeOut) {
                                if value.translation.width < swipeThreshold {
                                    offsetX = -100 // reveal delete
                                } else {
                                    offsetX = 0 // bounce back
                                }
                            }
                        }
                )
                .animation(.easeOut, value: offsetX + dragOffset)
        }
        .background(AppColors.sectionBackground(for: colorScheme))
        .clipped()
    }
}
