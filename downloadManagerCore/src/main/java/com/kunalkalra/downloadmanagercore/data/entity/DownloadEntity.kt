package com.kunalkalra.downloadmanagercore.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kunalkalra.downloadmanagercore.downloadManager.DownloadState

@Entity
data class DownloadEntity(
    @PrimaryKey(autoGenerate = false) val downloadId: String,
    val url: String,
    val fileName: String,
    val completeFilePath: String,
    val fileSize: Long?,
    val downloadState: DownloadState
)