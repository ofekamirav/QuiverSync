//
//  HomeView.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI
import Shared
import CoreLocation


struct HomeView: View {
    
    @Environment(\.colorScheme) var colorScheme

    
    let data : HomePageData
    @State private var isExpanded = true
    @State private var locationName: String = "Loading..."




    var body: some View {
        let todayForecast = data.weeklyForecast.first
        ScrollView {
            VStack(spacing: 16) {
                // Location header
                HStack {
                    Image(systemName: "location.fill")
                        .foregroundColor(AppColors.sandOrange)
                        .font(.system(size: 14))

                    Text(locationName)
                        .font(.title3)
                        .foregroundColor(AppColors.textPrimary(for: colorScheme))
                        .fontWeight(.semibold)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    Button(action: {
                        withAnimation(
                            .easeInOut(duration: 0.3)){
                                isExpanded.toggle()
                            }
                         }) {
                        Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                            .foregroundColor(.gray)
                    }
                }
                .contentShape(Rectangle()) // Make entire area tappable
                .padding(.vertical, 8)
                .padding(.horizontal)

                // Current Conditions
                if isExpanded {
                    CurrentConditionsView(
                        waveHeight: String(format: "%.1fft", todayForecast?.waveHeight ?? 0.0),
                        wind: String(format: "%.0f km/h", todayForecast?.windSpeed ?? 0.0),
                        swellPeriod: String(format: "%.1f s", todayForecast?.swellPeriod ?? 0.0)
                    )
                }

                if let surfboard = data.surfboard, let prediction = data.predictionForToday {
                    BoardRecommendationCardView(
                        surfboard: surfboard,
                        prediction: prediction
                    )
                }



                Text("Weekly Forecast")
                    .font(.subheadline)
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal)

                VStack(spacing: 12) {
                    ForEach(data.weeklyForecast.indices, id: \.self) { index in
                        let forecast = data.weeklyForecast[index]
                        let date = parseDate(forecast.date)
                        let day = dayOfWeek(from: date)
                        let shortDate = shortDateString(from: date)

                        ForecastItemView(
                            day: day,
                            date: shortDate,
                            waveHeight: String(format: "%.1fft", forecast.waveHeight),
                            wind: String(format: "%.0f km/h", forecast.windSpeed)
                        )
                    }
                }

            }
            .padding()
        }
        .background(AppColors.sectionBackground(for: colorScheme))
        .onAppear {
            let forecast = data.weeklyForecast.first
            print("Forecast Location Coordinates: \(forecast?.latitude ?? 0.0), \(forecast?.longitude ?? 0.0)")
            if let safeForecast = forecast {
                if(safeForecast.longitude == 0.0 && safeForecast.latitude == 0.0) {
                    locationName = "Unknown Spot"
                    return
                } else {
                    getPlaceNameFromCoordinates(latitude: safeForecast.latitude, longitude: safeForecast.longitude) { name in
                        DispatchQueue.main.async {
                            locationName = name ?? "Unknown Spot"
                        }
                    }
                }
            } else {
                locationName = "Unknown Spot"
            }
        }
    }
}

//#Preview{
//    HomeScreen()
//}


private func parseDate(_ dateString: String) -> Date {
    let formatter = DateFormatter()
    formatter.dateFormat = "yyyy-MM-dd" // Match your Kotlin format
    return formatter.date(from: dateString) ?? Date()
}

private func dayOfWeek(from date: Date) -> String {
    let formatter = DateFormatter()
    formatter.locale = Locale.current
    formatter.dateFormat = "EEE" // "Mon", "Tue", etc.
    return formatter.string(from: date)
}

private func shortDateString(from date: Date) -> String {
    let formatter = DateFormatter()
    formatter.locale = Locale.current
    formatter.dateFormat = "MMM d" // "Jul 14"
    return formatter.string(from: date)
}





func getPlaceNameFromCoordinates(latitude: Double, longitude: Double, completion: @escaping (String?) -> Void) {
    let location = CLLocation(latitude: latitude, longitude: longitude)
    CLGeocoder().reverseGeocodeLocation(location) { placemarks, error in
        if let error = error {
            print("❌ Reverse geocode failed: \(error.localizedDescription)")
            completion(nil)
            return
        }

        if let placemark = placemarks?.first {
            let name = placemark.locality ?? placemark.name ?? "Unknown Location"
            let country = placemark.country ?? ""
            let displayName = "\(name), \(country)"
            completion(displayName)
        } else {
            completion(nil)
        }
    }
}
