package com.kunalkalra.downloadmanagercore.utils

import kotlinx.coroutines.*

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

fun CoroutineScope.safeLaunch(block: suspend () -> Unit): Job {
    val coroutineExceptionHandler = CoroutineExceptionHandler {
            _, throwable ->
        logDebug(throwable.message)
    }

    return this.launch(coroutineExceptionHandler) {
        block.invoke()
    }
}