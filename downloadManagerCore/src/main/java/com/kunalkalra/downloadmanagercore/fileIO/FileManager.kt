package com.kunalkalra.downloadmanagercore.fileIO

import androidx.annotation.IntRange
import com.kunalkalra.downloadmanagercore.FileConstants
import com.kunalkalra.downloadmanagercore.utils.logDebug
import com.kunalkalra.downloadmanagercore.utils.logException
import com.kunalkalra.downloadmanagercore.utils.withIOContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import okio.*
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class FileManager : IFileOperations {

    override fun deleteFile(filePath: String): Boolean {
        val fileToDelete = File(filePath)
        return try {
            when (doesFileExist(filePath)) {
                true -> fileToDelete.delete()
                else -> false
            }
        } catch (e: SecurityException) {
            false
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
        try {
            logDebug("Content Length: ${body?.contentLength().toString()}")
            val sink = file.appendingSink().buffer()
            body?.let { safeResponseBody ->
                val source = safeResponseBody.source()
                writeToFileInChunksInternal(sink, source, chunkSize)
            }
        } catch (e: FileNotFoundException) {
            logException(e)
        } catch (e: IOException) {
            logException(e)
        } finally {
            body?.close()
        }
    }

    override suspend fun writeToFileInChunksWithSeek(
        file: File,
        body: ResponseBody?,
        @IntRange(
            from = 1L,
            to = FileConstants.DEFAULT_BUFFER_SIZE
        ) chunkSize: Long,
        seek: Long
    ) {
        try {
            val sink = file.appendingSink().buffer()
            body?.let { safeResponseBody ->
                val source = safeResponseBody.source().apply {
                    skip(seek)
                }
                writeToFileInChunksInternal(sink, source, chunkSize)
            }
        } catch (e: FileNotFoundException) {
            logException(e)
        } catch (e: IOException) {
            logException(e)
        } finally {
            body?.close()
        }
    }

    private suspend fun writeToFileInChunksInternal(
        sink: BufferedSink,
        source: BufferedSource,
        chunkSize: Long
    ) {
        withIOContext {
            val buffer = Buffer()
            var bytesRead = source.read(buffer, chunkSize)
            var totalBytesTransferred = bytesRead + 1
            while (bytesRead != -1L) {
                if(coroutineContext.job.isActive) {
                    sink.write(buffer, buffer.size)
                    bytesRead = source.read(buffer, chunkSize)
                    totalBytesTransferred += bytesRead
                } else {
                    return@withIOContext
                    sink.close()
                }
            }
            logDebug(totalBytesTransferred.toString())
            sink.close()
        }
    }
}