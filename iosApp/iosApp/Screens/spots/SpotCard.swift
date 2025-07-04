import SwiftUI
import Shared

public struct SpotCard: View {
    let favSpot: FavoriteSpot
    @Binding var selectedSpot: FavoriteSpot?
    @State private var isExpanded = false
    @State private var isShowingPopup = false  

    public var body: some View {
        ZStack {
            //  Base Card UI
            VStack(spacing: 16) {
                HStack {
                    VStack(alignment: .leading) {
                        Text(favSpot.name)
                            .font(.headline)
                            .foregroundColor(AppColors.deepBlue)

                        Text(favSpot.location)
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                    Spacer()
                    Button(action: {
                        withAnimation(.easeInOut(duration: 0.3)) {
                            isExpanded.toggle()
                        }
                    }) {
                        Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                            .foregroundColor(.gray)
                    }
                }
                .padding(.vertical, 8)
                .padding(.horizontal)

                if isExpanded {
                    ExtendCard(favSpot: favSpot, showPopup: {
                        withAnimation {
                            isShowingPopup = true
                        }
                    } , selectedSpot: $selectedSpot)
                }
            }
        }
        .padding()
        .blur(radius: selectedSpot != nil ? 6 : 0)
        
    }
}
