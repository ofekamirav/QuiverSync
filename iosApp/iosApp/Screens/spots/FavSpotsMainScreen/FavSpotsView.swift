//  FavSpotsView.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.



import SwiftUI
import Shared

public struct FavSpotsView: View {
    @Environment(\.colorScheme) var colorScheme

    let favSpots: FavSpotsData
    let favSpotsViewModel: FavSpotsViewModel

    @State private var selectedSpot: FavoriteSpot? = nil
    @State private var showAddSpotScreen = false
    @State private var spotToDelete: FavoriteSpot? = nil

    public var body: some View {
        ZStack(alignment: .bottomTrailing) {
            // 🔹 Background
            AppColors.sectionBackground(for: colorScheme)
                .ignoresSafeArea()

            if favSpots.spots.isEmpty {
                VStack {
                    Spacer()
                    Text("No favorite spots found")
                        .foregroundColor(AppColors.textPrimary(for: colorScheme))
                        .padding()
                    Spacer()
                }
            } else {
                // 🔹 Main List
                List {
                    ForEach(favSpots.spots, id: \.spotID) { spot in
                        ZStack {
                            SpotCard(
                                favSpot: spot,
                                favSpotsData: favSpots,
                                favSpotsViewModel: favSpotsViewModel,
                                selectedSpot: $selectedSpot
                            )
                            .padding()
                            .animation(.easeInOut(duration: 0.5), value: selectedSpot) // 👈 smoother toggle
                        }
                        .padding(.vertical, 2)
                        .padding(.horizontal, 8)
                        .background(AppColors.sectionBackground(for: colorScheme))
                        .swipeActions(edge: .trailing) {
                            Button(role: .destructive) {
                                withAnimation {
                                    spotToDelete = spot
                                }
                            } label: {
                                Image(systemName: "trash")
                            }
                            .tint(.red)
                        }
                        .listRowSeparator(.hidden)
                        .listRowInsets(EdgeInsets())
                        .listRowBackground(Color.clear)
                    }
                }
                .listStyle(.plain)
                .scrollContentBackground(.hidden)
                .background(AppColors.sectionBackground(for: colorScheme))
                .blur(radius: (selectedSpot != nil || spotToDelete != nil) ? 10 : 0)

            }

            // ➕ Add Button
            if selectedSpot == nil {
                Button(action: {
                    showAddSpotScreen = true
                }) {
                    Image(systemName: "plus")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.white)
                        .frame(width: 50, height: 50)
                        .background(AppColors.deepBlue)
                        .clipShape(Circle())
                        .shadow(radius: 4)
                }
                .padding(.trailing, 24)
                .padding(.bottom, 20)
                .sheet(isPresented: $showAddSpotScreen) {
                    AddSpotScreen(showAddSpotScreen: $showAddSpotScreen)
                }
            }

            // 📍 Weekly Forecast Popup
            if let spot = selectedSpot {
                ZStack {
                    WeeklyForecastPopup(
                        selectedSpot: $selectedSpot,
                        favSpotsData: favSpots,
                        favSpotsViewModel: favSpotsViewModel
                    )
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                }
                .zIndex(1)
                .onAppear {
                    favSpotsViewModel.onEvent(event: FavSpotsEventLoadWeekPredictions(favoriteSpot: spot))
                }
            }

            // 🗑 Confirm Deletion Popup
            if let spot = spotToDelete {
                ZStack {
                    Color.black.opacity(0.4).ignoresSafeArea()

                    VStack(spacing: 20) {
                        Text("Delete \"\(spot.name)\"?")
                            .font(.headline)
                            .foregroundColor(AppColors.textPrimary(for: colorScheme))
                            .multilineTextAlignment(.center)

                        HStack(spacing: 16) {
                            Button("Cancel") {
                                withAnimation {
                                    spotToDelete = nil
                                }
                            }
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(colorScheme == .dark ? AppColors.darkBorder : AppColors.borderGray)
                            .cornerRadius(12)
                            .foregroundColor(.white)

                            Button("Delete") {
                                withAnimation {
                                    favSpotsViewModel.onEvent(event: FavSpotsEventDeleteSpot(favoriteSpot: spot))
                                    spotToDelete = nil
                                }
                            }
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color.red)
                            .cornerRadius(12)
                            .foregroundColor(.white)
                        }
                    }
                    .padding()
                    .background(AppColors.cardColor(for: colorScheme).opacity(0.95))
                    .cornerRadius(20)
                    .padding(.horizontal, 32)
                }
                .zIndex(3)
                .transition(.opacity)
            }
        }
        .animation(.easeInOut, value: selectedSpot != nil || spotToDelete != nil)
    }
}
