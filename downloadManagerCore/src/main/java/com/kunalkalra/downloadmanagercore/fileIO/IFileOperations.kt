package com.kunalkalra.downloadmanagercore.fileIO

import okhttp3.ResponseBody
import java.io.File

interface IFileOperations {

    fun doesFileExist(filePath: String): Boolean

    fun deleteFile(filePath: String): Boolean

    suspend fun createFile(filePath: String): File?

    suspend fun writeToFile(file: File, body: ResponseBody?)

    suspend fun writeToFileInChunks(file: File, body: ResponseBody?, chunkSize: Long)

    suspend fun writeToFileInChunksWithSeek(file: File, body: ResponseBody?, chunkSize: Long, seek: Long)
}