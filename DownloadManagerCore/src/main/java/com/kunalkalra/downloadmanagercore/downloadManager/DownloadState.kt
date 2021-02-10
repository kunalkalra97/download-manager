package com.kunalkalra.downloadmanagercore.downloadManager

/**
 * Specifies the current state of the download Job
 * Start- The download job has started
 * Pause- The download job has been paused
 * Resume- The download job has been resumed from a paused state
 * Stop- The download job has been cancelled/completed
 */

sealed class DownloadState {
    object Start: DownloadState()
    object Pause: DownloadState()
    object Resume: DownloadState()
    object Stop: DownloadState()
}