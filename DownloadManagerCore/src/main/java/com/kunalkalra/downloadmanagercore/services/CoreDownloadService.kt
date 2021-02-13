package com.kunalkalra.downloadmanagercore.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationManagerCompat
import com.kunalkalra.downloadmanagercore.Actions.PAUSE_DOWNLOAD_ACTION
import com.kunalkalra.downloadmanagercore.Actions.RESUME_DOWNLOAD_ACTION
import com.kunalkalra.downloadmanagercore.Actions.START_DOWNLOAD_ACTION
import com.kunalkalra.downloadmanagercore.Actions.STOP_DOWNLOAD_ACTION
import com.kunalkalra.downloadmanagercore.HeaderConstants
import com.kunalkalra.downloadmanagercore.IntentConstants
import com.kunalkalra.downloadmanagercore.downloadManager.DownloadState
import com.kunalkalra.downloadmanagercore.downloadManager.exceptions.MimeTypeNotDetermined
import com.kunalkalra.downloadmanagercore.fileIO.FileManager
import com.kunalkalra.downloadmanagercore.downloadManager.models.CoreDownloadJobStatus
import com.kunalkalra.downloadmanagercore.downloadManager.models.CoreDownloadRequest
import com.kunalkalra.downloadmanagercore.network.OkHttpsNetworkManager
import com.kunalkalra.downloadmanagercore.network.models.SafeResult
import com.kunalkalra.downloadmanagercore.utils.MimeUtils
import com.kunalkalra.downloadmanagercore.utils.NotificationUtils
import com.kunalkalra.downloadmanagercore.utils.logDebug
import kotlinx.coroutines.*
import okhttp3.*
import java.util.concurrent.CancellationException

class CoreDownloadService : Service() {

    private var downloadServiceScope = MainScope()

    private val networkManager by lazy { OkHttpsNetworkManager() }
    private val fileManager by lazy { FileManager() }

    private var allDownloadsJobStatuses = mutableListOf<CoreDownloadJobStatus>()

    private val actionBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            logDebug(intent?.action)
        }
    }

    override fun onCreate() {
        registerReceiver(actionBroadcastReceiver, IntentFilter().apply {
            addAction(START_DOWNLOAD_ACTION)
            addAction(STOP_DOWNLOAD_ACTION)
            addAction(PAUSE_DOWNLOAD_ACTION)
            addAction(RESUME_DOWNLOAD_ACTION)
        })
    }

    override fun onDestroy() {
        downloadServiceScope.cancel(CancellationException("Download Service Stopped"))
        unregisterReceiver(actionBroadcastReceiver)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val coreDownloadRequest =
            intent.getParcelableExtra<CoreDownloadRequest>(IntentConstants.INTENT_DOWNLOAD)
        coreDownloadRequest?.let { safeCoreDownloadRequest ->
            try {
                val coreDownloadJobStatus = CoreDownloadJobStatus(
                    job = downloadServiceScope.launch { safeCoreDownloadRequest.downloadInternal() },
                    downloadRequest = safeCoreDownloadRequest,
                    downloadState = DownloadState.Start,
                    notificationId = safeCoreDownloadRequest.id
                )
                allDownloadsJobStatuses.add(coreDownloadJobStatus)
                updateNotification(coreDownloadJobStatus)
            } catch (e: MimeTypeNotDetermined) {
                logDebug(e.message)
            }

        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? = null


    private fun updateNotification(downloadJobStatus: CoreDownloadJobStatus) {

        val notification = when(downloadJobStatus.downloadState) {
            DownloadState.Start -> {
                NotificationUtils.getDownloadStartNotification(this, downloadJobStatus.downloadRequest)
            }

            else -> {
                // Todo("Create other states")
                NotificationUtils.getDownloadStartNotification(this, downloadJobStatus.downloadRequest)
            }
        }
        NotificationManagerCompat.from(this).notify(downloadJobStatus.notificationId, notification)
    }

    @Throws(MimeTypeNotDetermined::class)
    private suspend fun CoreDownloadRequest.downloadInternal() {
        val request = Request.Builder()
            .url(url)
            .build()
        logDebug("downloadInternal 1: ${Thread.currentThread().name}")
        when (val response = networkManager.requestResource(request = request)) {
            is SafeResult.Success -> {
                response.data?.let { safeResponse ->
                    when (val mimeType = MimeUtils.getExtensionFromResponse(safeResponse)) {
                        null -> throw MimeTypeNotDetermined()
                        else -> {
                            logDebug("downloadInternal 2: ${Thread.currentThread().name}")
                            val completePath = this@downloadInternal.getCompleteFilePath(mimeType)
                            val file = fileManager.createFile(completePath)
                            file?.let { safeFile ->
                                fileManager.writeToFile(safeFile, safeResponse.body)
                            }
                        }
                    }
                }
            }

            is SafeResult.Failure -> {
                logDebug("Could not fetch Resource from remote")
            }
        }

    }
}