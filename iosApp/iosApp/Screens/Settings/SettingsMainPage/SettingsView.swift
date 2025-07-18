//
//  SettingsView.swift
//  iosApp
//
//  Created by gal levi on 16/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared

struct SettingsView: View {
    @Environment(\.colorScheme) var colorScheme

    var viewModel: SettingsViewModel

    @State private var showDialog = false
    @State private var isLoading = false

    var onEditProfile: () -> Void = {}
    var onSecuritySettings: () -> Void = {}
    var onNotificationsSettings: () -> Void = {}
    var onHelpSupport: () -> Void = {}
    var onLogout: () -> Void = {}

    var body: some View {
        ZStack {
            // 🌗 Background adapts to theme
            AppColors.sectionBackground(for: colorScheme)
                .ignoresSafeArea()

            VStack(spacing: 16) {
                Spacer().frame(height: 24)

                SettingItemView(
                    iconName: "pencil",
                    label: "Edit Profile Details",
                    onClick: onEditProfile
                )

                SettingItemView(
                    iconName: "lock.shield",
                    label: "Security & Privacy",
                    onClick: onSecuritySettings
                )

                SettingItemView(
                    iconName: "bell",
                    label: "Notifications",
                    onClick: onNotificationsSettings
                )

                SettingItemView(
                    iconName: "questionmark.circle",
                    label: "Help & Support",
                    onClick: onHelpSupport
                )

                Spacer()

                Button(action: {
                    showDialog = true
                }) {
                    HStack {
                        Image(systemName: "rectangle.portrait.and.arrow.right")
                            .foregroundColor(.white)
                        Text("Log Out")
                            .foregroundColor(.white)
                            .fontWeight(.semibold)
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(AppColors.error)
                    .cornerRadius(12)
                }
                .padding(.bottom, 16)
            }
            .padding(.horizontal, 16)

            if isLoading {
                Color.black.opacity(0.3)
                    .ignoresSafeArea()
                LoadingAnimationView(
                    animationName: "quiver_sync_loading_animation",
                    size: 240
                )
            }

            if showDialog {
                CustomDialogView(
                    title: "Log Out",
                    message: "Are you sure you want to log out?",
                    onConfirm: {
                        showDialog = false
                        isLoading = true
                        viewModel.logout {
                            isLoading = false
                            
                            onLogout()
                        }
                    },
                    onCancel: {
                        showDialog = false
                    }
                )
            }
        }
        .background(AppColors.sectionBackground(for: colorScheme))
    }
}
