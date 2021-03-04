package com.kunalkalra.downloadmanagercore.fileIO

import okhttp3.ResponseBody
import java.io.File

interface IFileOperations {

    suspend fun createFile(filePath: String): File?

    fun doesFileExist(filePath: String): Boolean

    suspend fun writeToFile(file: File, body: ResponseBody?)

    suspend fun writeToFileInChunks(file: File, body: ResponseBody?, chunkSize: Long)

    fun deleteFile(filePath: String): Boolean
}