package com.kunalkalra.downloadmanagercore.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.kunalkalra.downloadmanagercore.Actions
import com.kunalkalra.downloadmanagercore.NotificationConstants.DEFAULT_NOTIFICATION_CHANNEL_DESCRIPTION
import com.kunalkalra.downloadmanagercore.NotificationConstants.DEFAULT_NOTIFICATION_CHANNEL_ID
import com.kunalkalra.downloadmanagercore.NotificationConstants.DEFAULT_NOTIFICATION_CHANNEL_NAME
import com.kunalkalra.downloadmanagercore.R
import com.kunalkalra.downloadmanagercore.downloadManager.DownloadState
import com.kunalkalra.downloadmanagercore.downloadManager.models.CoreDownloadRequest
import kotlin.random.Random

object NotificationUtils {

    fun getDownloadStartNotification(context: Context, coreDownloadRequest: CoreDownloadRequest): Notification {
        return NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle(coreDownloadRequest.fileName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(getActionFor(DownloadState.Pause, context))
            .addAction(getActionFor(DownloadState.Stop, context))
            .build()
    }

    fun getDownloadCompleteNotification(context: Context, coreDownloadRequest: CoreDownloadRequest): Notification {
        return NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle(coreDownloadRequest.fileName)
            .setContentText(context.getString(R.string.label_download_complete))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(getActionFor(DownloadState.Pause, context))
            .addAction(getActionFor(DownloadState.Stop, context))
            .build()
    }

    fun registerNotificationChannel(
        context: Context,
        channelName: String = DEFAULT_NOTIFICATION_CHANNEL_NAME,
        channelDescription: String = DEFAULT_NOTIFICATION_CHANNEL_DESCRIPTION,
        channelImportance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        /*
            Default Factory to create notification channel
            Parameters: id, name, importance
         */
        val notificationChannel = NotificationChannel(
            DEFAULT_NOTIFICATION_CHANNEL_ID, channelName, channelImportance
        ).apply {
            description = channelDescription
        }

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            notificationChannel
        )
    }

    private fun getActionFor(state: DownloadState, context: Context): NotificationCompat.Action {
        return when(state) {
            DownloadState.Resume -> {
                NotificationCompat.Action.Builder(
                    R.drawable.ic_play, context.getString(R.string.label_resume), getPendingIntent(state, context)
                ).build()
            }

            DownloadState.Start -> {
                NotificationCompat.Action.Builder(
                    R.drawable.ic_play, context.getString(R.string.label_start), getPendingIntent(state, context)
                ).build()
            }

            DownloadState.Pause -> {
                NotificationCompat.Action.Builder(
                    R.drawable.ic_pause, context.getString(R.string.label_pause), getPendingIntent(state, context)
                ).build()
            }

            DownloadState.Stop -> {
                NotificationCompat.Action.Builder(
                    R.drawable.ic_stop, context.getString(R.string.label_cancel), getPendingIntent(state, context)
                ).build()
            }
        }

    }

    private fun getPendingIntent(state: DownloadState, context: Context): PendingIntent {
        val intent = when(state) {
            DownloadState.Resume -> {
               Intent(Actions.RESUME_DOWNLOAD_ACTION)
            }

            DownloadState.Start -> {
                Intent(Actions.START_DOWNLOAD_ACTION)
            }

            DownloadState.Pause -> {
                Intent(Actions.PAUSE_DOWNLOAD_ACTION)
            }

            DownloadState.Stop -> {
                Intent(Actions.STOP_DOWNLOAD_ACTION)
            }
        }
        return PendingIntent.getBroadcast(context, Random.nextInt(), intent, 0)
    }

}