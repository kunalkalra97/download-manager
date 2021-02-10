package com.kunalkalra.downloadmanagercore.models

import com.kunalkalra.downloadmanagercore.downloadManager.DownloadState
import kotlinx.coroutines.Job

data class CoreDownloadJobStatus(
    var job: Job,
    var downloadRequest: CoreDownloadRequest,
    var downloadState: DownloadState,
    var notificationId: Int,
)