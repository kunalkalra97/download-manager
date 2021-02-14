package com.kunalkalra.downloadmanagercore.downloadManager.models

import android.os.Environment
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.random.Random

@Suppress("DEPRECATION")
@Parcelize
data class CoreDownloadRequest(
    val url: String,
    val fileName: String,
    val destinationDirectory: String = Environment.DIRECTORY_DOWNLOADS,
    val id: Int = Random.nextInt(),
    var completeFilePath: String? = null
): Parcelable {

    fun updateCompleteFilePathWithMimeType(mimeTypeExtension: String) {
        val destinationDirectoryPath = Environment.getExternalStoragePublicDirectory(destinationDirectory).path
        completeFilePath = "$destinationDirectoryPath/$fileName.$mimeTypeExtension"
    }
}