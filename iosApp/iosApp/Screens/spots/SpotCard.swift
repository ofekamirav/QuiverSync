import SwiftUI
import Shared

public struct SpotCard: View {
    @Environment(\.colorScheme) var colorScheme

    let favSpot: FavoriteSpot
    let favSpotsData: FavSpotsData
    let favSpotsViewModel: FavSpotsViewModel
    @Binding var selectedSpot: FavoriteSpot?
//    let onRequestDelete: () -> Void

    @State private var isExpanded = false
    @State private var isShowingPopup = false

    public var body: some View {
        ZStack(alignment: .topTrailing) {
            VStack(spacing: 12) {
                HStack(alignment: .top, spacing: 12) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(favSpot.name)
                            .font(.system(.headline, design: .rounded))
                            .foregroundColor(AppColors.textPrimary(for: colorScheme))

                        Text(favSpot.name)
                            .font(.caption)
                            .foregroundColor(.gray)
                            .lineLimit(2)
                    }

                    Spacer()

                    Button(action: {
                        withAnimation(.easeInOut(duration: 0.25)) {
                            isExpanded.toggle()
                        }
                    }) {
                        Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                            .foregroundColor(AppColors.textPrimary(for: colorScheme).opacity(0.6))
                    }
                    .buttonStyle(PlainButtonStyle())
                }
                .padding(.top, 12)
                .padding(.horizontal)

                if isExpanded {
                    Divider()
                    ExtendCard(
                        favSpot: favSpot,
                        favSpotData: favSpotsData,
                        favSpotsViewModel: favSpotsViewModel,
                        showPopup: {
                            withAnimation {
                                isShowingPopup = true
                            }
                        },
                        selectedSpot: $selectedSpot
                    )
                    .opacity(isExpanded ? 1 : 0)
                    .frame(height: isExpanded ? nil : 0)
                    .clipped()
                    .padding(.horizontal)
                    .padding(.bottom, 20)

                }
            }
            .background(AppColors.cardColor(for: colorScheme)) // 💡 ensure it's always colored
        }
        .padding()
        .background(AppColors.cardColor(for: colorScheme))
        .cornerRadius(16)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(colorScheme == .dark ? AppColors.darkBorder : AppColors.borderGray, lineWidth: 1)
        )
        .background(
            Color.clear
                .contentShape(Rectangle())
                .allowsHitTesting(false) // prevent card's background from hijacking touches
        )


    }
}
