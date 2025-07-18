//
//  CustomDatePickerView.swift
//  iosApp
//
//  Created by gal levi on 17/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct CustomDatePickerView: View {
    let title: String
    let selectedDate: String
    let onDateSelected: (String) -> Void
    let errorMessage: String?

    @State private var date: Date = Date()

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.headline)

            DatePicker(
                "",
                selection: Binding(
                    get: { formattedDateToDate(selectedDate) ?? Date() },
                    set: {
                        onDateSelected(formatDate($0))
                    }
                ),
                displayedComponents: [.date]
            )
            .labelsHidden()
            .datePickerStyle(.compact)

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
