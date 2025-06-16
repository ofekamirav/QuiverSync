package org.example.quiversync.features

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent

actual open class BaseViewModel: KoinComponent {
    actual val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun clear(){
        scope.cancel()
    }
}