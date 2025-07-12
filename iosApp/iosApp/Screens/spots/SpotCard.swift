import SwiftUI
import Shared

public struct SpotCard: View {
    let favSpot: FavoriteSpot
    let favSpotsData: FavSpotsData
    let favSpotsViewModel: FavSpotsViewModel
    @Binding var selectedSpot: FavoriteSpot?
    let onRequestDelete: () -> Void

    @State private var isExpanded = false
    @State private var isShowingPopup = false

    public var body: some View {
        ZStack(alignment: .topTrailing) {
            VStack(spacing: 12) {
                HStack(alignment: .top, spacing: 12) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(favSpot.name)
                            .font(.system(.headline, design: .rounded))
                            .foregroundColor(AppColors.deepBlue)

                        Text(favSpot.name)
                            .font(.caption)
                            .foregroundColor(.gray)
                            .lineLimit(2)
                    }

                    Spacer()

                    Button(action: {
                        onRequestDelete()
                    }) {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.red)
                            .font(.system(size: 18, weight: .bold))
                            .padding(.trailing, 4)
                    }

                    Button(action: {
                        withAnimation(.easeInOut(duration: 0.25)) {
                            isExpanded.toggle()
                        }
                    }) {
                        Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                            .foregroundColor(.gray)
                    }
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
                    .transition(.opacity.combined(with: .slide))
                    .padding(.horizontal)
                    .padding(.bottom, 20) // prevent content under bottom bar
                }
            }

        }
        .padding()
        .blur(radius: selectedSpot != nil ? 6 : 0)
    }
}
