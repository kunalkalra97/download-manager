package com.kunalkalra.downloadmanagercore

import com.kunalkalra.downloadmanagercore.downloadManager.DownloadState

object HeaderConstants {
    const val USER_AGENT = "USER_AGENT"
    const val REFERRER = "referrer"
    const val CONTENT_LENGTH = "Content-Length"
    const val CONTENT_TYPE = "content-type"
    const val COOKIE = "Cookie"
}

object IntentConstants {
    const val INTENT_DOWNLOAD = "INTENT_DOWNLOAD"
    const val INTENT_URL = "INTENT_URL"
    const val INTENT_FILENAME = "INTENT_FILENAME"
    const val INTENT_DESTINATION_DIRECTORY = "INTENT_DESTINATION_DIRECTORY"
}


object NotificationConstants {
    const val DEFAULT_NOTIFICATION_CHANNEL_ID = "com.kunalkalra.downloadmanagercore-notificationChanel"
    const val DEFAULT_NOTIFICATION_CHANNEL_NAME = "CORE_DOWNLOAD_MANAGER_CHANNEL"
    const val DEFAULT_NOTIFICATION_CHANNEL_DESCRIPTION = "Core Download Manager Notification Channel"
    const val DOWNLOAD_ID = "DOWNLOAD_ID"
}

object Actions {
    const val START_DOWNLOAD_ACTION = "com.kunalkalra.downloadmanagercore.start_action"
    const val PAUSE_DOWNLOAD_ACTION = "com.kunalkalra.downloadmanagercore.pause_action"
    const val RESUME_DOWNLOAD_ACTION = "com.kunalkalra.downloadmanagercore.resume_action"
    const val STOP_DOWNLOAD_ACTION = "com.kunalkalra.downloadmanagercore.stop_action"
}

object FileConstants {
    const val DEFAULT_BUFFER_SIZE = 8192L
    const val CUSTOM_BUFFER_SIZE = 20480L
}

object StateStrings {
    const val start = "start"
    const val pause = "pause"
    const val resume ="resume"
    const val stop = "stop"
    const val complete = "complete"
    const val undetermined = "undetermined"
}