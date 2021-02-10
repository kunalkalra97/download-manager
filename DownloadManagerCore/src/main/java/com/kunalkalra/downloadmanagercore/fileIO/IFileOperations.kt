package com.kunalkalra.downloadmanagercore.fileIO

import okhttp3.ResponseBody
import java.io.File

interface IFileOperations {

    fun createFile(path: String): File?

    fun writeToFile(file: File, body: ResponseBody?)

    fun writeToFileInChunks(file: File, body: ResponseBody?, chunkSize: Long)
}