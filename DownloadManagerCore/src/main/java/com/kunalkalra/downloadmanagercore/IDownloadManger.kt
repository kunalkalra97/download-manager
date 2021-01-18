package com.kunalkalra.downloadmanagercore

interface IDownloadManger {

    val permissions: Array<String>

    fun download(coreDownloadRequest: CoreDownloadRequest, cookie: String = "")

}