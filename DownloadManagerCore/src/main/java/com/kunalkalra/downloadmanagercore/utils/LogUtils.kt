package com.kunalkalra.downloadmanagercore.utils

import android.util.Log

private const val TAG = "kalrk-test"

fun logDebug(message: String?) {
    message?.let { Log.d(TAG, it) }
}

fun logException(exception: Exception) {
    exception.message?.let { Log.d(TAG, "Failed With Exception -- $it") }
}