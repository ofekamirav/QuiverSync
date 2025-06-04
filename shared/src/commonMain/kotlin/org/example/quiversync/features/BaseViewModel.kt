package org.example.quiversync.features

import kotlinx.coroutines.CoroutineScope

expect open class BaseViewModel(){
    val scope: CoroutineScope
}