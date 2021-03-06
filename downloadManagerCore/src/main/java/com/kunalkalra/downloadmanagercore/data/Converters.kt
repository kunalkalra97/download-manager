package com.kunalkalra.downloadmanagercore.data

import androidx.room.TypeConverter
import com.kunalkalra.downloadmanagercore.downloadManager.DownloadState

class Converters {
    @TypeConverter
    fun fromDownloadStateToStateString(downloadState: DownloadState): String {
        return downloadState.stateString
    }

    @TypeConverter
    fun fromStateStringToDownloadState(string: String): DownloadState {
        return DownloadState.getStateFromString(string)
    }
}