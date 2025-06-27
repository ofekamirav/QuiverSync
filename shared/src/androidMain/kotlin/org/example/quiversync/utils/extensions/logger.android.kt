package org.example.quiversync.utils.extensions

import android.util.Log

actual fun platformLogger(tag: String, message: String) {
    Log.i(tag, message)
}