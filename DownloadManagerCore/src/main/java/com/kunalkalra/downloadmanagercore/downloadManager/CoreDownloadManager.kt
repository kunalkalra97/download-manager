package com.kunalkalra.downloadmanagercore.downloadManager

import android.Manifest.permission.*
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import com.kunalkalra.downloadmanagercore.IntentConstants.INTENT_DOWNLOAD
import com.kunalkalra.downloadmanagercore.downloadManager.models.CoreDownloadRequest
import com.kunalkalra.downloadmanagercore.services.CoreDownloadService
import com.kunalkalra.downloadmanagercore.utils.NotificationUtils
import com.kunalkalra.downloadmanagercore.utils.PermissionUtils.validatePermissions
import com.kunalkalra.downloadmanagercore.utils.logException


class CoreDownloadManager(
    private val applicationContext: Context
): IDownloadManger {

    init {
        NotificationUtils.registerNotificationChannel(applicationContext)
    }

    override val permissions: Array<String>
        get() = if (SDK_INT >= P)
            arrayOf(INTERNET, WRITE_EXTERNAL_STORAGE, FOREGROUND_SERVICE)
        else
            arrayOf(INTERNET, WRITE_EXTERNAL_STORAGE)


    override fun download(coreDownloadRequest: CoreDownloadRequest) {
        try {
            applicationContext.validatePermissions(permissions)
            val serviceIntent = Intent(applicationContext, CoreDownloadService::class.java).apply {
                putExtra(INTENT_DOWNLOAD, coreDownloadRequest)
            }
            applicationContext.startService(serviceIntent)
        } catch (exception: SecurityException) {
            logException(exception)
        }

    }
}
