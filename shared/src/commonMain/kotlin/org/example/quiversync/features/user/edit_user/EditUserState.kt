package org.example.quiversync.features.user.edit_user

sealed class EditUserState {
    object Loading : EditUserState()
    object Success : EditUserState()
    data class Editing(
        val form: EditUserFormData,
    ) : EditUserState()
    data class Error(val message: String) : EditUserState()
}
