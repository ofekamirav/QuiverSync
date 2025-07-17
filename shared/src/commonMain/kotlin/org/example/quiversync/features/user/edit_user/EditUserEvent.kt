package org.example.quiversync.features.user.edit_user

import org.example.quiversync.features.quiver.add_board.AddBoardEvent

sealed interface EditUserDetailsEvent {
    data class onNameChange(val name: String) : EditUserDetailsEvent
    data class onHeightChange(val height: Double) : EditUserDetailsEvent
    data class onWeightChange(val weight: Double) : EditUserDetailsEvent
    data class onSurfLevelChange(val surfLevel: String) : EditUserDetailsEvent
    data class ProfileImageSelected(val bytes: ByteArray) : EditUserDetailsEvent
    data class profileImageIOSChanged(val imageURL : String) : EditUserDetailsEvent
    object onSubmit : EditUserDetailsEvent
}