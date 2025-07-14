//
//  QuiverView.swift
//  iosApp
//
//  Created by gal levi on 20/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import SwiftUI
import Shared

struct QuiverView: View {
    let boards: [Surfboard]
    let boardToPublish: Surfboard?
    let boardViewModel: QuiverViewModel
    let onAddClick: () -> Void
    
    @Binding var selectedBoard: Surfboard?
    @Binding var boardToDelete: Surfboard?
    
    @State private var showAddBoardScreen = false

    


    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            if boards.isEmpty {
                VStack(spacing: 12) {
                    Spacer()
                    Text("No Surfboards Found")
                        .font(.title)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)
                    Text("Add your first surfboard by clicking the button below.")
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 32)
                    Spacer()
                }
            } else {
                ScrollView {
                    LazyVGrid(columns: [GridItem(.adaptive(minimum: 160), spacing: 16)], spacing: 16) {
                        ForEach(boards, id: \.id) { board in
                            BoardCard(
                                board: board,
                                onClick: { selectedBoard = board },
                                onPublishToggle: { surfboard, isChecked in
                                    if isChecked && !(surfboard.isRentalPublished?.boolValue ?? false) {
                                        boardViewModel.onEvent(event: QuiverEventShowPublishDialog(surfboardId: surfboard.id))
                                    } else if !isChecked && (surfboard.isRentalPublished?.boolValue ?? false) {
                                        boardViewModel.onEvent(event: QuiverEventUnpublishSurfboard(surfboardId: surfboard.id))
                                    }
                                }
                            )
                        }
                    }
                    .padding(16)
                }
            }

            // ➕ Add button
            Button(action: {
                showAddBoardScreen = true
            }) {
                Image(systemName: "plus")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(.white)
                    .frame(width: 60, height: 60)
                    .background(Color.blue)
                    .clipShape(Circle())
                    .shadow(radius: 6)
            }
            .padding(24)

            // 📋 Detail Dialog
            if let selected = selectedBoard {
                SurfboardDetailDialog(
                    board: selected,
                    isPresented: true,
                    onDismiss: { selectedBoard = nil },
                    onDelete: { boardToDelete = $0 }
                )
            }

            // ❌ Delete Dialog
            if let toDelete = boardToDelete {
                CustomDialog(
                    title: "Delete Surfboard",
                    message: "Are you sure you want to delete the surfboard: '\(toDelete.model)'?",
                    onDismiss: { boardToDelete = nil },
                    onConfirm: {
                        boardViewModel.onEvent(event: QuiverEventOnDeleteClick(surfboardId: toDelete.id))
                        selectedBoard = nil
                        boardToDelete = nil
                    },
                    confirmText: "Delete",
                    cancelText: "Cancel"
                )
            }

            // 💸 Publish Dialog
            if let toPublish = boardToPublish {
                PublishSurfboardDialog(
                    surfboard: toPublish,
                    isPresented: true,
                    onConfirm: { pricePerDay in
                        boardViewModel.onEvent(
                            event: QuiverEventConfirmPublish(
                                surfboardId: toPublish.id,
                                pricePerDay: pricePerDay
                            )
                        )
                    },
                    onDismiss: {
                        boardViewModel.onEvent(event: QuiverEventDismissPublishDialog())
                    }
                )
                .onAppear(){
                    print("Publish dialog appeared for surfboard: \(toPublish.model)")
                }
            }
        }
        .background(AppColors.background)
        .sheet(isPresented: $showAddBoardScreen) {
            AddBoardScreen(
                onBackRequested: {
                    showAddBoardScreen = false
                }
            )
        }

    }
}

