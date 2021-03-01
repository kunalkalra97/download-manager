package com.kunalkalra.downloadmanagercore.fileIO

import com.kunalkalra.downloadmanagercore.utils.logException
import com.kunalkalra.downloadmanagercore.utils.withIOContext
import okhttp3.ResponseBody
import okio.*
import java.io.File
import java.lang.Long.min

class FileManager: IFileOperations {

    override suspend fun createFile(path: String): File? {
        return withIOContext {
            val file = File(path)
            try {
                file.createNewFile()
                file
            } catch (e: IOException) {
                logException(e)
                null
            } catch (e: SecurityException) {
                logException(e)
                null
            }
        }
    }

    override fun doesFileExist(filePath: String): Boolean {
        val file = File(filePath)
        return try {
            file.exists()
        } catch (e: SecurityException) {
            logException(e)
            false
        }
    }

    override suspend fun writeToFile(file: File, body: ResponseBody?) {
        withIOContext {
            try {
                val bufferedSink = file.sink().buffer()
                body?.let { safeResponseBody ->
                    val safeSource = safeResponseBody.source()
                    bufferedSink.writeAll(safeSource)
                }
                bufferedSink.close()
            } catch (e: FileNotFoundException) {
                logException(e)
            } catch (e: IOException) {
                logException(e)
            } finally {
                body?.close()
            }
        }
    }

    override suspend fun writeToFileInChunks(file: File, body: ResponseBody?, chunkSize: Int) {
        withIOContext {
            try {
                val sink = file.sink().buffer()
                body?.let { safeResponseBody ->
                    val source = safeResponseBody.source().readByteArray()
                    var remainingContentLength = safeResponseBody.contentLength()
                    var offset = 0
                    while(chunkSize < remainingContentLength) {
                        sink.write(source, offset, chunkSize)
                        offset += chunkSize + 1
                        remainingContentLength -= chunkSize
                    }
                    sink.write(source, offset, body.contentLength().toInt())
                }
            } catch (e: FileNotFoundException) {
                logException(e)
            } catch (e: IOException) {
                logException(e)
            } finally {
                body?.close()
            }
        }
    }

    override fun deleteFile(filePath: String): Boolean {
        val fileToDelete = File(filePath)
        return try {
            when(doesFileExist(filePath)) {
                true -> fileToDelete.delete()
                else -> false
            }
        } catch (e: SecurityException) {
            false
        }
    }
}