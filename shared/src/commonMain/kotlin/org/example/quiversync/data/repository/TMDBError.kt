package org.example.quiversync.data.repository

import org.example.quiversync.data.local.Error

data class TMDBError(
    override val message: String
): Error
