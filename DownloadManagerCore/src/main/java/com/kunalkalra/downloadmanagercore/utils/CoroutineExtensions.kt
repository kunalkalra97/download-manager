package com.kunalkalra.downloadmanagercore.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> withIOContext(block: suspend() -> T): T? {
    return withContext(Dispatchers.IO) {
        try {
            block.invoke()
        } catch (e: Exception) {
            logDebug(e.message)
            null
        }
    }
}