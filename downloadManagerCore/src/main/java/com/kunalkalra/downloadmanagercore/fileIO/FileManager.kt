package com.kunalkalra.downloadmanagercore.fileIO

import androidx.annotation.IntRange
import com.kunalkalra.downloadmanagercore.FileConstants
import com.kunalkalra.downloadmanagercore.utils.logException
import com.kunalkalra.downloadmanagercore.utils.withIOContext
import okhttp3.ResponseBody
import okio.*
import java.io.File

class FileManager: IFileOperations {

    override suspend fun createFile(filePath: String): File? {
        return withIOContext {
            val file = File(filePath)
            try {
                file.apply {
                    createNewFile()
                }
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

    override suspend fun writeToFileInChunks(
        file: File,
        body: ResponseBody?,
        @IntRange(
            from = 1L,
            to = FileConstants.DEFAULT_BUFFER_SIZE
        ) chunkSize: Long
    ) {
        withIOContext {
            try {
                val sink = file.appendingSink().buffer()
                val buffer = Buffer()
                body?.let { safeResponseBody ->
                    val source = safeResponseBody.source()
                    var bytesRead = source.read(buffer, chunkSize)
                    while (bytesRead != -1L) {
                        sink.write(buffer, buffer.size)
                        bytesRead = source.read(buffer, chunkSize)
                    }
                    sink.close()
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