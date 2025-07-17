//
//  SettingsWrapper.swift
//  iosApp
//
//  Created by gal levi on 16/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI
import Shared
import Foundation


extension SettingsScreen {
    
    @MainActor
    class SettingsViewModelWrapper: ObservableObject {
        
        let viewModel: SettingsViewModel
        
        init(){
            self.viewModel = KoinKt.settingsViewModel()
        }
        
    }
}
    
    


