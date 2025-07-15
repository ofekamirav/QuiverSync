package org.example.quiversync.features.user

import org.example.quiversync.domain.usecase.UploadImageUseCase
import org.example.quiversync.domain.usecase.user.GetUserProfileUseCase
import org.example.quiversync.domain.usecase.register.UpdateUserProfileUseCase
import org.example.quiversync.domain.usecase.user.CheckUserAuthMethodUseCase
import org.example.quiversync.domain.usecase.user.GetBoardsNumberUseCase
import org.example.quiversync.domain.usecase.user.GetSpotsNumberUseCase
import org.example.quiversync.domain.usecase.user.LogoutUseCase
import org.example.quiversync.domain.usecase.user.SendPasswordResetEmailUseCase
import org.example.quiversync.domain.usecase.user.UpdatePasswordUseCase
import org.example.quiversync.domain.usecase.user.UpdateProfileDetailsUseCase

data class UserUseCases(
    val getUserProfileUseCase: GetUserProfileUseCase,
    val updateUserProfileUseCase: UpdateUserProfileUseCase,
    val logoutUseCase: LogoutUseCase,
    val getBoardsNumberUseCase: GetBoardsNumberUseCase,
    val uploadImageUseCase: UploadImageUseCase,
    val updateProfileDetailsUseCase: UpdateProfileDetailsUseCase,
    val checkUserAuthMethod: CheckUserAuthMethodUseCase,
    val updatePasswordUseCase: UpdatePasswordUseCase,
    val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
    val getSpotsNumberUseCase: GetSpotsNumberUseCase
)
