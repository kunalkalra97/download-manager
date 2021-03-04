package com.kunalkalra.downloadmanagercore.network.models

sealed class SafeResult<out T> {
    class Success<T>(val data: T?) : SafeResult<T>()
    class Failure(
        val throwable: Throwable = Exception("Unknown Error"),
        val message: String? = throwable.message ?: ""
    ) : SafeResult<Nothing>()
}