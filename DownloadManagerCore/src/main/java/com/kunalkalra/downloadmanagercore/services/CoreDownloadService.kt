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
import com.kunalkalra.downloadmanagercore.fileIO.FileManager
import com.kunalkalra.downloadmanagercore.models.CoreDownloadJobStatus
import com.kunalkalra.downloadmanagercore.models.CoreDownloadRequest
import com.kunalkalra.downloadmanagercore.network.NetworkManager
import com.kunalkalra.downloadmanagercore.network.SafeResult
import com.kunalkalra.downloadmanagercore.utils.NotificationUtils
import com.kunalkalra.downloadmanagercore.utils.logDebug
import kotlinx.coroutines.*
import okhttp3.*
import java.util.concurrent.CancellationException

class CoreDownloadService : Service() {

    private var downloadServiceScope = MainScope()

    private val networkManager by lazy { NetworkManager() }
    private val fileManager by lazy { FileManager() }

    private var allDownloadsJobStatus = mutableListOf<CoreDownloadJobStatus>()

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
        val coreDownloadRequest = intent.getParcelableExtra<CoreDownloadRequest>(IntentConstants.INTENT_DOWNLOAD)
        coreDownloadRequest?.let { safeCoreDownloadRequest ->
            val coreDownloadJobStatus = CoreDownloadJobStatus(
                    job = safeCoreDownloadRequest.downloadInternal(),
                    downloadRequest = safeCoreDownloadRequest,
                    downloadState = DownloadState.Start,
                    notificationId = safeCoreDownloadRequest.id
            )
            allDownloadsJobStatus.add(coreDownloadJobStatus)
            updateNotification(coreDownloadJobStatus)
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
                // Todo("Don't do anything else for now")
                NotificationUtils.getDownloadStartNotification(this, downloadJobStatus.downloadRequest)
            }
        }
        NotificationManagerCompat.from(this).notify(downloadJobStatus.notificationId, notification)
    }

    private fun CoreDownloadRequest.downloadInternal(): Job {
        val request = Request.Builder()
                .url(url)
                .build()

        return downloadServiceScope.launch {
            logDebug(Thread.currentThread().name)
            when (val result = networkManager.requestResource(request = request)) {
                is SafeResult.Success -> {
                    result.data?.let { response ->
                        val mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(response.headers.toMultimap()[HeaderConstants.CONTENT_TYPE]?.get(0))
                        val completePath = this@downloadInternal.getCompleteFilePath(mimeType!!)
                        val file = fileManager.createFile(completePath)
                        file?.let { safeFile ->
                            fileManager.writeToFile(safeFile, response.body)
                        }
                    }
                }

                is SafeResult.Failure -> {
                    logDebug("Could not fetch Resource from remote")
                }
            }
        }

    }
}