//  FavSpotsView.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.

import SwiftUI
import Shared

public struct FavSpotsView: View {
    let favSpots: FavSpotsData
    let favSpotsViewModel: FavSpotsViewModel

    @State private var selectedSpot: FavoriteSpot? = nil
    @State private var showAddSpotScreen = false
    @State private var spotToDelete: FavoriteSpot? = nil

    public var body: some View {
        ZStack(alignment: .bottomTrailing) {
            VStack(spacing: 0) {
                if favSpots.spots.isEmpty {
                    Spacer()
                    Text("No favorite spots found")
                        .foregroundColor(AppColors.deepBlue)
                        .padding()
                    Spacer()
                } else {
                    ScrollView(showsIndicators: false) {
                        LazyVStack(spacing: 16) {
                            ForEach(favSpots.spots, id: \ .self) { spot in
                                SpotCard(
                                    favSpot: spot,
                                    favSpotsData: favSpots,
                                    favSpotsViewModel: favSpotsViewModel,
                                    selectedSpot: $selectedSpot,
                                    onRequestDelete: {
                                        withAnimation {
                                            spotToDelete = spot
                                        }
                                    }
                                )
                                .background(AppColors.foamWhite.opacity(0.95))
                                .clipShape(RoundedRectangle(cornerRadius: 20))
                                .shadow(color: .black.opacity(0.05), radius: 5, x: 0, y: 2)
                                .padding(.horizontal, 16)
                            }
                        }
                        .padding(.top, 16)
                        .padding(.bottom, 20) // prevent content under bottom bar
                    }
                    .blur(radius: selectedSpot != nil || spotToDelete != nil ? 10 : 0)
                }
            }
            
            if(selectedSpot==nil){
                Button(action: {
                    showAddSpotScreen = true
                }) {
                    Image(systemName: "plus")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.white)
                        .frame(width: 36, height: 36)
                        .background(AppColors.deepBlue)
                        .clipShape(Circle())
                        .shadow(radius: 4)
                }
                .padding(.trailing, 24)
                .padding(.bottom, 20) // padding above bottom bar
                .sheet(isPresented: $showAddSpotScreen) {
                    AddSpotScreen(showAddSpotScreen: $showAddSpotScreen)
                }
            }

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

            if let spot = spotToDelete {
                ZStack {
                    Color.black.opacity(0.4)
                        .ignoresSafeArea()

                    VStack(spacing: 20) {
                        Text("Delete \"\(spot.name)\"?")
                            .font(.headline)
                            .foregroundColor(.black)
                            .multilineTextAlignment(.center)

                        HStack(spacing: 16) {
                            Button("Cancel") {
                                withAnimation {
                                    spotToDelete = nil
                                }
                            }
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color.gray.opacity(0.7))
                            .cornerRadius(12)
                            .foregroundColor(.white)

                            Button("Delete") {
                                withAnimation {
                                    print("Deleting spot: \(spot.name)")
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
                    .background(Color.white.opacity(0.95))
                    .cornerRadius(20)
                    .padding(.horizontal, 32)
                }
                .zIndex(3)
                .transition(.opacity)
            }
        }
        .animation(.easeInOut, value: selectedSpot != nil || spotToDelete != nil)
        .background(AppColors.background)
    }
}

