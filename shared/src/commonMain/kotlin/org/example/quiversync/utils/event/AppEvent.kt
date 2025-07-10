package org.example.quiversync.utils.event

sealed class AppEvent {
    object ProfileUpdated : AppEvent()
    object BoardAdded : AppEvent()
}