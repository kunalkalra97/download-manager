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
import com.kunalkalra.downloadmanagercore.NotificationConstants.DOWNLOAD_ID
import com.kunalkalra.downloadmanagercore.R
import com.kunalkalra.downloadmanagercore.downloadManager.models.CoreDownloadRequest
import kotlin.random.Random

object NotificationUtils {

    fun getDownloadStartNotification(
        context: Context,
        coreDownloadRequest: CoreDownloadRequest
    ): Notification {
        return NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle(coreDownloadRequest.fileName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                getActionFor(
                    NotificationAction.ActionPause(coreDownloadRequest.id),
                    context
                )
            )
            .addAction(getActionFor(NotificationAction.ActionStop(coreDownloadRequest.id), context))
            .build()
    }

    fun getDownloadCompleteNotification(
        context: Context,
        coreDownloadRequest: CoreDownloadRequest
    ): Notification {
        return NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle(coreDownloadRequest.fileName)
            .setContentText(context.getString(R.string.label_download_complete))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    fun getDownloadStoppedNotification(
        context: Context,
        coreDownloadRequest: CoreDownloadRequest
    ): Notification {
        return NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle(coreDownloadRequest.fileName)
            .setContentText(context.getString(R.string.label_download_stopped))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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

    private fun getActionFor(
        notificationAction: NotificationAction,
        context: Context
    ): NotificationCompat.Action {
        return when (notificationAction) {
            is NotificationAction.ActionResume -> {
                NotificationCompat.Action.Builder(
                    R.drawable.ic_play,
                    context.getString(R.string.label_resume),
                    getPendingIntent(notificationAction, context)
                ).build()
            }

            is NotificationAction.ActionPause -> {
                NotificationCompat.Action.Builder(
                    R.drawable.ic_pause,
                    context.getString(R.string.label_pause),
                    getPendingIntent(notificationAction, context)
                ).build()
            }

            is NotificationAction.ActionStop -> {
                NotificationCompat.Action.Builder(
                    R.drawable.ic_stop,
                    context.getString(R.string.label_cancel),
                    getPendingIntent(notificationAction, context)
                ).build()
            }
        }

    }

    private fun getPendingIntent(
        notificationAction: NotificationAction,
        context: Context
    ): PendingIntent {
        val intent = when (notificationAction) {
            is NotificationAction.ActionResume -> {
                Intent(Actions.RESUME_DOWNLOAD_ACTION)
            }

            is NotificationAction.ActionStop -> {
                Intent(Actions.STOP_DOWNLOAD_ACTION)
            }

            is NotificationAction.ActionPause -> {
                Intent(Actions.PAUSE_DOWNLOAD_ACTION)
            }
        }.putExtra(DOWNLOAD_ID, notificationAction.notificationID)
        return PendingIntent.getBroadcast(context, Random.nextInt(), intent, 0)
    }

    sealed class NotificationAction(val notificationID: Int) {
        class ActionPause(notificationId: Int) : NotificationAction(notificationId)
        class ActionStop(notificationId: Int) : NotificationAction(notificationId)
        class ActionResume(notificationId: Int) : NotificationAction(notificationId)
    }

}