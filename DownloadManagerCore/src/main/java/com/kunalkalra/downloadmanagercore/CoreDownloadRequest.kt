package com.kunalkalra.downloadmanagercore

import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.net.Uri
import android.os.Environment
import kotlin.random.Random

data class CoreDownloadRequest(
    val url: String,
    val fileName: String,
    val contentLength: Long? = null,
    val coreDownloadRequestHeaders: CoreDownloadRequestHeaders? = null,
    val destinationDirectory: String = Environment.DIRECTORY_DOWNLOADS,
    val id: Long = Random.nextLong()
) {
    fun toAndroidDownloadRequest(cookie: String?): DownloadManager.Request {
        val androidDownloadRequest = DownloadManager.Request(Uri.parse(this.url))
            .setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(destinationDirectory, fileName)

        coreDownloadRequestHeaders?.let { coreDownloadRequestHeaders ->
            androidDownloadRequest.addHeaders(coreDownloadRequestHeaders, cookie)
        }

        return androidDownloadRequest
    }

    private fun DownloadManager.Request.addHeaders(headers: CoreDownloadRequestHeaders, cookie: String?) {
        with(headers) {
            this.contentType?.let { this@addHeaders.addRequestHeader(HeaderConstants.CONTENT_TYPE, it) }
            this.referrer?.let { this@addHeaders.addRequestHeader(HeaderConstants.REFERRER, it) }
            cookie?.let { this@addHeaders.addRequestHeader(HeaderConstants.COOKIE, it) }
        }
    }
}

data class CoreDownloadRequestHeaders(
    val contentType: String? = null,
    val referrer: String? = null,
)