package com.kunalkalra.downloadmanagercore.fileIO

import androidx.annotation.IntRange
import com.kunalkalra.downloadmanagercore.FileConstants
import com.kunalkalra.downloadmanagercore.utils.logDebug
import com.kunalkalra.downloadmanagercore.utils.logException
import com.kunalkalra.downloadmanagercore.utils.withIOContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.job
import okhttp3.ResponseBody
import okio.*
import java.io.File

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

    override fun writeToFileInChunks(file: File, body: ResponseBody?, chunkSize: Long): Flow<Long> {
        logDebug(body?.contentLength())
        flow {
            try {
                val sink = file.appendingSink().buffer()
                body?.let { safeResponseBody ->
                    val source = safeResponseBody.source()
                    val flow = writeToFileInChunksInternal(sink, source, chunkSize)
                    val buffer = Buffer()
                    var totalBytesTransferred = 0L
                    while (!source.exhausted()) {
                        if (currentCoroutineContext().job.isActive) {
                            val bytesRead = source.read(buffer, chunkSize)
                            if (bytesRead != -1L) {
                                sink.write(buffer, buffer.size)
                                totalBytesTransferred += bytesRead
                            }
                        } else {
                            sink.close()
                            return@flow
                        }
                        emit(totalBytesTransferred)
                    }
                    sink.close()
                }
            } catch (e: FileNotFoundException) {
                logException(e)
            } catch (e: IOException) {
                logException(e)
            } catch (e: Exception) {
                logException(e)
            } finally {
                body?.close()
            }
        }
        return emptyFlow()
    }

    override fun writeToFileInChunksWithSeek(
        file: File,
        body: ResponseBody?,
        @IntRange(
            from = 1L,
            to = FileConstants.DEFAULT_BUFFER_SIZE
        ) chunkSize: Long,
        seek: Long
    ): Flow<Long> {
        return flow<Long> {
            try {
                val sink = file.appendingSink().buffer()
                body?.let { safeResponseBody ->
                    val source = safeResponseBody.source().apply {
                        skip(seek)
                    }
                    val buffer = Buffer()
                    var totalBytesTransferred = 0L
                    while (!source.exhausted()) {
                        if (currentCoroutineContext().job.isActive) {
                            val bytesRead = source.read(buffer, chunkSize)
                            if (bytesRead != -1L) {
                                sink.write(buffer, buffer.size)
                                totalBytesTransferred += bytesRead
                            }
                        } else {
                            sink.close()
                            return@flow
                        }
                        emit(totalBytesTransferred)
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
        }.flowOn(Dispatchers.IO)
    }

    private fun writeToFileInChunksInternal(
        sink: BufferedSink,
        source: BufferedSource,
        chunkSize: Long
    ): Flow<Long> {
        return flow<Long> {
            val buffer = Buffer()
            var totalBytesTransferred = 0L
            while (!source.exhausted()) {
                if (currentCoroutineContext().job.isActive) {
                    val bytesRead = source.read(buffer, chunkSize)
                    if (bytesRead != -1L) {
                        sink.write(buffer, buffer.size)
                        totalBytesTransferred += bytesRead
                    }
                } else {
                    sink.close()
                    return@flow
                }
                emit(totalBytesTransferred)
            }
            sink.close()
        }.flowOn(Dispatchers.IO)
    }

}