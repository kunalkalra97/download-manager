package com.kunalkalra.downloadmanagercore.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.kunalkalra.downloadmanagercore.Actions.PAUSE_DOWNLOAD_ACTION
import com.kunalkalra.downloadmanagercore.Actions.RESUME_DOWNLOAD_ACTION
import com.kunalkalra.downloadmanagercore.Actions.START_DOWNLOAD_ACTION
import com.kunalkalra.downloadmanagercore.Actions.STOP_DOWNLOAD_ACTION
import com.kunalkalra.downloadmanagercore.IntentConstants
import com.kunalkalra.downloadmanagercore.NotificationConstants.DOWNLOAD_ID
import com.kunalkalra.downloadmanagercore.downloadManager.ActionHandler
import com.kunalkalra.downloadmanagercore.downloadManager.DownloadState
import com.kunalkalra.downloadmanagercore.downloadManager.NotificationActionCallback
import com.kunalkalra.downloadmanagercore.downloadManager.exceptions.FileExistsException
import com.kunalkalra.downloadmanagercore.downloadManager.exceptions.MimeTypeNotDeterminedException
import com.kunalkalra.downloadmanagercore.fileIO.FileManager
import com.kunalkalra.downloadmanagercore.downloadManager.models.CoreDownloadJobStatus
import com.kunalkalra.downloadmanagercore.downloadManager.models.CoreDownloadRequest
import com.kunalkalra.downloadmanagercore.network.OkHttpsNetworkManager
import com.kunalkalra.downloadmanagercore.usecases.fileIOUseCases.UseCaseDeleteFile
import com.kunalkalra.downloadmanagercore.usecases.fileIOUseCases.UseCaseWriteToExistingFileInChunks
import com.kunalkalra.downloadmanagercore.usecases.fileIOUseCases.UseCaseWriteToFile
import com.kunalkalra.downloadmanagercore.usecases.networkUseCases.UseCaseRequestResource
import com.kunalkalra.downloadmanagercore.utils.*
import kotlinx.coroutines.*
import okhttp3.*
import java.util.concurrent.CancellationException
import kotlin.jvm.Throws

class CoreDownloadService : Service() {

    private var downloadServiceScope = MainScope()

    private val networkManager by lazy { OkHttpsNetworkManager() }
    private val fileManager by lazy { FileManager() }
    private val useCaseWriteToFile = UseCaseWriteToFile(fileManager)
    private val useCaseDeleteFile = UseCaseDeleteFile(fileManager)
    private val useCaseWriteToExistingFileInChunks = UseCaseWriteToExistingFileInChunks(fileManager)
    private val useCaseRequestResource = UseCaseRequestResource(networkManager)

    private val notificationActionCallbackHandler = ActionHandler(NotificationCallbackHandler())

    private var allDownloadsJobStatuses = hashMapOf<Int, CoreDownloadJobStatus>()

    private val actionBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            val downloadId = intent?.getIntExtra(DOWNLOAD_ID, 0)
            notificationActionCallbackHandler.handleActions(action, downloadId)
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { safeIntent ->
            val coreDownloadRequest =
                safeIntent.getParcelableExtra<CoreDownloadRequest>(IntentConstants.INTENT_DOWNLOAD)
            coreDownloadRequest?.let { safeCoreDownloadRequest ->
                try {
                    val coreDownloadJobStatus = CoreDownloadJobStatus(
                        job = downloadServiceScope.safeLaunch { safeCoreDownloadRequest.downloadInternal() },
                        downloadRequest = safeCoreDownloadRequest,
                        downloadState = DownloadState.Start,
                    )
                    val downloadId = coreDownloadJobStatus.downloadRequest.id
                    addToAllDownloadsStatuses(downloadId, coreDownloadJobStatus)
                    handleNotificationUpdates(downloadId)
                } catch (e: MimeTypeNotDeterminedException) {
                    logDebug(e.message)
                }

            }
        }
        // Todo: Check for restarting service
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @Throws(MimeTypeNotDeterminedException::class)
    private suspend fun CoreDownloadRequest.downloadInternal() {
        val request = Request.Builder()
            .url(url)
            .build()
        try {
            val response = useCaseRequestResource.performOperation(request)
            response?.let {
                when (val mimeType = MimeUtils.getExtensionFromResponse(response)) {
                    null -> throw MimeTypeNotDeterminedException()
                    else -> {
                        val completePath = this.getCompleteFilePathWithMimeType(mimeType)
                        useCaseWriteToFile.performOperation(
                            UseCaseWriteToFile.getFileWriteParams(
                                completePath,
                                response.body
                            )
                        )
                        updateDownloadState(this.id, DownloadState.Complete)
                        handleNotificationUpdates(this.id)
                    }
                }
            }
        } catch (e: FileExistsException) {
            logException(e)
        }

    }


    private fun handleNotificationUpdates(downloadId: Int?) {
        val downloadJobStatus = allDownloadsJobStatuses[downloadId]
        downloadJobStatus?.let {
            val notification = when (downloadJobStatus.downloadState) {
                DownloadState.Start -> {
                    NotificationUtils.getDownloadStartNotification(
                        this,
                        downloadJobStatus.downloadRequest
                    )
                }

                DownloadState.Stop -> {
                    NotificationUtils.getDownloadStoppedNotification(
                        this,
                        downloadJobStatus.downloadRequest
                    )
                }

                DownloadState.Pause -> {
                    NotificationUtils.getDownloadPausedNotification(
                        this,
                        downloadJobStatus.downloadRequest
                    )
                }

                DownloadState.Complete -> {
                    NotificationUtils.getDownloadCompleteNotification(
                        this, downloadJobStatus.downloadRequest
                    )
                }

                else -> {
                    // Todo("Create other states")
                    NotificationUtils.getDownloadStartNotification(
                        this,
                        downloadJobStatus.downloadRequest
                    )
                }
            }
            val id = downloadJobStatus.downloadRequest.id
            NotificationManagerCompat.from(this).notify(id, notification)
        }
    }

    private fun updateDownloadState(downloadID: Int?, downloadState: DownloadState) {
        allDownloadsJobStatuses[downloadID].apply {
            this?.let {
                this.downloadState = downloadState
            }
        }
    }

    private fun addToAllDownloadsStatuses(downloadId: Int, coreDownloadJobStatus: CoreDownloadJobStatus) {
        allDownloadsJobStatuses[downloadId] = coreDownloadJobStatus
    }

    inner class NotificationCallbackHandler: NotificationActionCallback {

        override fun onActionStop(downloadId: Int?) {
            val downloadStatus = allDownloadsJobStatuses[downloadId]
            downloadStatus?.let { safeDownloadStatus ->
                cancelJob(safeDownloadStatus)
                deleteFile(safeDownloadStatus)
                updateDownloadState(downloadId, DownloadState.Stop)
                handleNotificationUpdates(downloadId)
            }
        }

        override fun onActionPause(downloadId: Int?) {
            val downloadStatus = allDownloadsJobStatuses[downloadId]
            downloadStatus?.let { safeDownloadStatus ->
                cancelJob(safeDownloadStatus)
                updateDownloadState(downloadId, DownloadState.Pause)
                handleNotificationUpdates(downloadId)
            }
        }

        override fun onActionResume(downloadId: Int?) {
            val downloadStatus = allDownloadsJobStatuses[downloadId]
            downloadStatus?.let { safeDownloadStatus ->
                updateDownloadState(downloadId, DownloadState.Start)
                handleNotificationUpdates(downloadId)
                downloadServiceScope.safeLaunch {
                    val coreDownloadRequest = safeDownloadStatus.downloadRequest
                    val request = Request.Builder().url(coreDownloadRequest.url).build()
                    try {
                        val response = useCaseRequestResource.performOperation(request)
                        response?.let {
                            useCaseWriteToExistingFileInChunks.performOperation(
                                UseCaseWriteToExistingFileInChunks.getFileWriteParams(
                                    coreDownloadRequest.completeFilePath!!,
                                    response.body,
                                    5000L,
                                    safeDownloadStatus.bytesTransferred
                                )
                            )
                            updateDownloadState(downloadId, DownloadState.Complete)
                            handleNotificationUpdates(downloadId)
                        }
                    } catch (e: FileExistsException) {
                        logException(e)
                    }
                }
            }
        }

        private fun cancelJob(safeDownloadStatus: CoreDownloadJobStatus) {
            val downloadJob = safeDownloadStatus.job
            downloadJob.cancel()

        }

        private fun deleteFile(safeDownloadStatus: CoreDownloadJobStatus) {
            val downloadPath = safeDownloadStatus.downloadRequest.completeFilePath
            downloadPath?.let { safeDownloadPath ->
                useCaseDeleteFile.performOperation(safeDownloadPath)
            }
        }

    }
}