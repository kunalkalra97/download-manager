package com.kunalkalra.downloadmanagercore.utils

import android.webkit.MimeTypeMap
import com.kunalkalra.downloadmanagercore.HeaderConstants
import okhttp3.Response

object MimeUtils {

    private val mimeTypeMap = MimeTypeMap.getSingleton()

    fun getExtensionFromResponse(networkResponse: Response): String? {
        val headers = networkResponse.headers.toMultimap()
        val contentType = headers[HeaderConstants.CONTENT_TYPE]?.get(0)
        return mimeTypeMap.getExtensionFromMimeType(contentType)
    }
}