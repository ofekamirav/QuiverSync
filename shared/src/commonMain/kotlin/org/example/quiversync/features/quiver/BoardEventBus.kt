package org.example.quiversync.features.quiver

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object BoardAddedEvent

object BoardEventBus {
    private val _events = MutableSharedFlow<BoardAddedEvent>()
    val events: SharedFlow<BoardAddedEvent> = _events.asSharedFlow()

    suspend fun emitBoardAdded() {
        _events.emit(BoardAddedEvent)
    }
}