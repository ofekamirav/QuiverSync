package org.example.quiversync

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform