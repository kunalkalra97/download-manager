package com.kunalkalra.downloadmanagercore.downloadManager

import com.kunalkalra.downloadmanagercore.StateStrings

/**
 * Specifies the current state of the download Job
 * Start- The download job has started
 * Pause- The download job has been paused
 * Resume- The download job has been resumed from a paused state
 * Stop- The download job has been cancelled
 * Complete- The download job has completed corresponding download
 * Undetermined- Could not infer download state
 */

sealed class DownloadState(val stateString: String) {

    companion object {
        fun getStateFromString(string: String): DownloadState {
            return when(string) {
                StateStrings.start -> Start
                StateStrings.pause -> Pause
                StateStrings.resume -> Resume
                StateStrings.stop -> Stop
                StateStrings.complete -> Complete
                else -> Undetermined
            }
        }
    }

    object Start: DownloadState(StateStrings.start)
    object Pause: DownloadState(StateStrings.pause)
    object Resume: DownloadState(StateStrings.resume)
    object Stop: DownloadState(StateStrings.stop)
    object Complete: DownloadState(StateStrings.complete)
    object Undetermined: DownloadState(StateStrings.undetermined)
}