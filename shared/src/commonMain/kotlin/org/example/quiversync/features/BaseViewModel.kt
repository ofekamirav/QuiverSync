package org.example.quiversync.features

import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent

expect open class BaseViewModel(): KoinComponent{
    val scope: CoroutineScope
}