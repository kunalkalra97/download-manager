package com.kunalkalra.downloadmanagercore.downloadManager.models

import com.kunalkalra.downloadmanagercore.downloadManager.DownloadState
import kotlinx.coroutines.Job

data class CoreDownloadJobStatus(
    var job: Job,
    var downloadRequest: CoreDownloadRequest,
    var downloadState: DownloadState,
    var bytesTransferred: Long = 0
)