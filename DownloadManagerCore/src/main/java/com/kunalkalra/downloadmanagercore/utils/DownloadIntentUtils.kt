package com.kunalkalra.downloadmanagercore.utils

import android.content.Intent
import androidx.core.os.bundleOf
import com.kunalkalra.downloadmanagercore.ExtraConstants.EXTRA_DOWNLOAD_ID
import com.kunalkalra.downloadmanagercore.IntentConstants.INTENT_DESTINATION_DIRECTORY
import com.kunalkalra.downloadmanagercore.IntentConstants.INTENT_DOWNLOAD
import com.kunalkalra.downloadmanagercore.IntentConstants.INTENT_FILENAME
import com.kunalkalra.downloadmanagercore.IntentConstants.INTENT_URL
import com.kunalkalra.downloadmanagercore.models.CoreDownloadRequest

object DownloadIntentUtils {
    fun Intent.putDownloadExtra(coreDownloadRequest: CoreDownloadRequest) {
        with(coreDownloadRequest) {
            putExtra(INTENT_DOWNLOAD, bundleOf(
                INTENT_URL to url,
                INTENT_FILENAME to fileName,
                INTENT_DESTINATION_DIRECTORY to destinationDirectory,
                EXTRA_DOWNLOAD_ID to id
            ))
        }
    }
}