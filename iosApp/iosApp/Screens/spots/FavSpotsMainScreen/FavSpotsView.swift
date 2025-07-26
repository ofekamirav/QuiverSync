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
    @State private var showAsForecastOnly = false


    public var body: some View {
        
        
        ZStack(alignment: .bottomTrailing) {
            // 🔹 Background
            AppColors.sectionBackground(for: colorScheme)
                .ignoresSafeArea()

            if favSpots.spots.isEmpty {
                EmptyStateView(
                    title: "No Favorite Spots Yet",
                    message: "Save your favorite surf spots to quickly access forecasts and plan your sessions.",
                    buttonText: "Add Spot",
                    systemImageName: "mappin.and.ellipse",
                    onButtonTap: {
                        showAddSpotScreen = true
                    }
                )
            } else {
                // 🔹 Main List
                ScrollView {
                    if !favSpots.boards.isEmpty {
                            Toggle(isOn: $showAsForecastOnly) {
                                Text(showAsForecastOnly ? "Showing Forecast Only" : "Showing Smart Match View")
                                    .font(.subheadline)
                                    .foregroundColor(AppColors.textPrimary(for: colorScheme))
                            }
                            .padding(.horizontal)
                            .padding(.top, 8)
                            .onAppear(){
                                print("FavSpotsView: Boards available for spots: \(favSpots.boards)")
                            }
                    }
                    LazyVStack(spacing: 0) {
                        ForEach(favSpots.spots, id: \.spotID) { spot in

                            if favSpots.boards.isEmpty || showAsForecastOnly {
                                SwipeToDeleteCard(
                                    content: {
                                        ForecastOnlyCard(
                                            spot: spot,
                                            favSpotsData: favSpots
                                        )
                                    },
                                    onDelete: {
                                        spotToDelete = spot
                                    }
                                )
                                .padding(.vertical, 2)
                                .padding(.horizontal, 8)
                                .background(AppColors.sectionBackground(for: colorScheme))
                                .onAppear(){
                                    print("FavSpotsView: No boards available for spot \(spot.name)")
                                    print("FavSpotsView: Boards count: \(favSpots)")
                                }
                            } else {
                                SwipeToDeleteCard(
                                    content: {
                                        SpotCard(
                                            favSpot: spot,
                                            favSpotsData: favSpots,
                                            favSpotsViewModel: favSpotsViewModel,
                                            selectedSpot: $selectedSpot
                                        )
                                        .padding()
                                    },
                                    onDelete: {
                                        spotToDelete = spot
                                    }
                                )
                                .padding(.vertical, 2)
                                .padding(.horizontal, 8)
                                .background(AppColors.sectionBackground(for: colorScheme))
                            }
                        }
                    }
                }
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
                                    favSpotsViewModel.onEvent(event: FavSpotsEventDeleteSpot(favoriteSpot: spot, snackBarDurationMillis: 30))
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







