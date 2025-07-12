package org.example.quiversync.features.spots

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SpotAddedEvent

object SpotEventBus {
    private val _events = MutableSharedFlow<SpotAddedEvent>()
    val events: SharedFlow<SpotAddedEvent> = _events.asSharedFlow()

    suspend fun emitBoardAdded() {
        _events.emit(SpotAddedEvent)
    }
}