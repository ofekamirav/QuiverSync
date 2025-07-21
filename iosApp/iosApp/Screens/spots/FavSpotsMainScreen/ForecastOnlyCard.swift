import SwiftUI
import Shared

struct ForecastOnlyCard: View {
    @Environment(\.colorScheme) var colorScheme

    let spot: FavoriteSpot
    let favSpotsData: FavSpotsData

    @State private var isExpanded = false

    private var forecast: DailyForecast? {
        favSpotsData.weeklyForecastForSpecificSpot.first {
            round($0.latitude * 10_000) == round(spot.spotLatitude * 10_000) &&
            round($0.longitude * 10_000) == round(spot.spotLongitude * 10_000)
        }
    }

    var body: some View {
        if let forecast = forecast {
            VStack(spacing: 12) {
                // Header
                HStack {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(spot.name)
                            .font(.system(.headline, design: .rounded))
                            .foregroundColor(AppColors.textPrimary(for: colorScheme))

                        Label(
                            title: {
                                Text("\(forecast.waveHeight, specifier: "%.1f") m wave")
                            },
                            icon: {
                                Image(systemName: "waveform.path.ecg")
                            }
                        )
                        .font(.subheadline)
                        .foregroundColor(.gray)
                    }

                    Spacer()

                    Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                        .foregroundColor(.gray)
                }
                .contentShape(Rectangle())
                .onTapGesture {
                    withAnimation(.easeInOut) {
                        isExpanded.toggle()
                    }
                }

                // Expanded details
                if isExpanded {
                    Divider()
                        .transition(.opacity)

                    VStack(alignment: .leading, spacing: 10) {
                        DetailRow(
                            icon: "wind",
                            label: "Wind",
                            value: String(format: "%.1f km/h", forecast.windSpeed)
                        )
                        DetailRow(
                            icon: "clock",
                            label: "Swell Period",
                            value: String(format: "%.1f s", forecast.swellPeriod)
                        )
                        DetailRow(
                            icon: "location.north.line",
                            label: "Wind Dir",
                            value: String(format: "%.0f°", forecast.windDirection)
                        )
                        DetailRow(
                            icon: "arrow.triangle.merge",
                            label: "Swell Dir",
                            value: String(format: "%.0f°", forecast.swellDirection)
                        )
                    }
                    .font(.caption)
                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                    .transition(.opacity.combined(with: .move(edge: .top)))
                }
            }
            .padding()
            .background(AppColors.cardColor(for: colorScheme))
            .cornerRadius(16)
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(colorScheme == .dark ? AppColors.darkBorder : AppColors.borderGray, lineWidth: 1)
            )
            .onAppear(){
                // Ensure the forecast is loaded when the view appears
                print("forecastOnlyCard: \(spot) and the forecast is \(forecast)")
            }
        } else {
            EmptyView()
        }
    }
}

private struct DetailRow: View {
    let icon: String
    let label: String
    let value: String

    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: icon)
                .foregroundColor(.blue)
            Text("\(label):")
                .fontWeight(.medium)
            Spacer()
            Text(value)
        }
    }
}
