package com.kunalkalra.downloadmanagercore.downloadManager

import com.kunalkalra.downloadmanagercore.Actions


interface NotificationActionCallback {
    fun onActionStop(downloadId: Int?)
    fun onActionPause(downloadId: Int?)
    fun onActionResume(downloadId: Int?)
}

class ActionHandler(private val notificationActionCallback: NotificationActionCallback) {

    fun handleActions(action: String?, downloadId: Int?) {
        when (action) {
            Actions.STOP_DOWNLOAD_ACTION -> {
                notificationActionCallback.onActionStop(downloadId)
            }
            Actions.PAUSE_DOWNLOAD_ACTION -> {
                notificationActionCallback.onActionPause(downloadId)
            }
            Actions.RESUME_DOWNLOAD_ACTION -> {
                notificationActionCallback.onActionResume(downloadId)
            }
        }
    }
}

