package com.kunalkalra.downloadmanagercore.utils

import android.util.Log
import com.kunalkalra.downloadmanagercore.BuildConfig

private const val TAG = "kalrk-test"

fun <T> logDebug(message: T?) {
    if (BuildConfig.DEBUG) {
        message?.toString()?.let {
            Log.d(TAG, it)
        }
    }
}

fun logException(exception: Exception) {
    if(BuildConfig.DEBUG) {
        exception.message?.let { Log.d(TAG, "Failed With Exception -- $it") }
    }
}