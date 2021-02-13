package com.kunalkalra.downloadmanagercore.downloadManager

import com.kunalkalra.downloadmanagercore.downloadManager.models.CoreDownloadRequest

interface IDownloadManger {

    val permissions: Array<String>

    fun download(coreDownloadRequest: CoreDownloadRequest)

}