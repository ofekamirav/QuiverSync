package org.example.quiversync.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent

actual open class BaseViewModel: ViewModel(), KoinComponent {
    actual val scope: CoroutineScope = viewModelScope
}