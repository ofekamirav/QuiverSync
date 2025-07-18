//
//  SettingScreen.swift
//  iosApp
//
//  Created by gal levi on 16/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import Shared
import Foundation

struct SettingsScreen: View {
    @ObservedObject private(set) var viewModel = SettingsViewModelWrapper()

    var onEditProfile: () -> Void
    var onSecuritySettings: () -> Void
    var onNotificationsSettings: () -> Void
    var onHelpSupport: () -> Void
    var onLogout: () -> Void

    var body: some View {
        SettingsView(
            viewModel: viewModel.viewModel,
            onEditProfile: onEditProfile,
            onSecuritySettings: onSecuritySettings,
            onNotificationsSettings: onNotificationsSettings,
            onHelpSupport: onHelpSupport,
            onLogout: onLogout
        )
    }
}
