package com.kunalkalra.downloadmanagercore.utils

import android.content.Context
import android.content.pm.PackageManager

object PermissionUtils {
    fun Context.validatePermissions(permissions: Array<String>) {
        if(permissions.asIterable().all { permission ->
            checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }) else throw SecurityException("Necessary Permissions not granted")
    }
}