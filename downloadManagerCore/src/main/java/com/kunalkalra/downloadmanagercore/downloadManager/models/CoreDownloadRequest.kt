package com.kunalkalra.downloadmanagercore.downloadManager.models

import android.os.Environment
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.random.Random

@Parcelize
data class CoreDownloadRequest(
    val url: String,
    val fileName: String,
    val destinationDirectory: String = Environment.DIRECTORY_DOWNLOADS,
    val id: Int = Random.nextInt(),
    var completeFilePath: String? = null,
    var mimeType: String? = null
): Parcelable {
    fun getCompleteFilePathWithMimeType(mimeTypeExtension: String): String {
        val destinationDirectoryPath = Environment.getExternalStoragePublicDirectory(destinationDirectory).path
        completeFilePath = "$destinationDirectoryPath/$fileName.$mimeTypeExtension"
        mimeType = mimeTypeExtension
        return "$destinationDirectoryPath/$fileName.$mimeTypeExtension"
    }
}