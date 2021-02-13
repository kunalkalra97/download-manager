package com.kunalkalra.downloadmanagercore.network.models

sealed class SafeResult<out T> {
    class Success<T>(val data: T?) : SafeResult<T>()
    class Failure(
        val exception: Exception? = Exception("Unknown Error"),
        val message: String? = exception?.message ?: ""
    ) : SafeResult<Nothing>()
}