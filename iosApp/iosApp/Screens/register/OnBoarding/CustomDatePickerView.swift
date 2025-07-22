//
//  CustomDatePickerView.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct CustomDatePickerView: View {
    @Environment(\.colorScheme) var colorScheme

    let title: String
    let selectedDate: String
    let onDateSelected: (String) -> Void
    let errorMessage: String?

    @State private var date: Date = Date()

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.headline)
                .foregroundColor(AppColors.textPrimary(for: colorScheme))


            DatePicker(
                "",
                selection: $date,
                displayedComponents: [.date]
            )
            .labelsHidden()
            .datePickerStyle(.compact)
            .onChange(of: date) {
                onDateSelected(formatDate($0))
            }
            .foregroundColor(AppColors.textPrimary(for: colorScheme))
            .onAppear {
                if let parsed = formattedDateToDate(selectedDate) {
                    date = parsed
                }
            }

            if let error = errorMessage {
                Text(error)
                    .font(.caption)
                    .foregroundColor(.red)
            }
        }
    }

    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: date)
    }

    private func formattedDateToDate(_ string: String) -> Date? {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.date(from: string)
    }
}
