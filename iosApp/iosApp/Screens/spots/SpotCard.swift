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
                }
                .contentShape(Rectangle()) // Make entire area tappable
                .onTapGesture {
                    withAnimation(.easeInOut(duration: 0.25)) {
                        isExpanded.toggle()
                    }
                }
                .padding(.top, 12)
                .padding(.horizontal)

                if isExpanded {
                    Divider()
                        .opacity(isExpanded ? 1 : 0)
                        .animation(.easeInOut(duration: 0.3), value: isExpanded)

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
                    .frame(maxHeight: isExpanded ? .infinity : 0)
                    .clipped()
                    .padding(.horizontal)
                    .padding(.bottom, isExpanded ? 20 : 0)
                    .animation(.easeInOut(duration: 0.3), value: isExpanded)

                }

            }
            .background(AppColors.cardColor(for: colorScheme)) // 💡 ensure it's always colored
            .animation(.easeInOut(duration: 0.3), value: isExpanded)

        }
        .padding()
        .background(AppColors.cardColor(for: colorScheme))
        .cornerRadius(16)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(colorScheme == .dark ? AppColors.darkBorder : AppColors.borderGray, lineWidth: 1)
        )



    }
}
