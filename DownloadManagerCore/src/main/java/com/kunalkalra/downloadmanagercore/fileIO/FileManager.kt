package com.kunalkalra.downloadmanagercore.fileIO

import com.kunalkalra.downloadmanagercore.utils.logDebug
import com.kunalkalra.downloadmanagercore.utils.logException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import okio.FileNotFoundException
import okio.IOException
import okio.buffer
import okio.sink
import java.io.File

class FileManager: IFileOperations {

    override suspend fun createFile(path: String): File? {
        return withContext(Dispatchers.IO) {
            logDebug("createFile 1: ${Thread.currentThread().name}")
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

    override suspend fun writeToFile(file: File, body: ResponseBody?) {
        return withContext(Dispatchers.IO) {
            try {
                logDebug("write To File 1: ${Thread.currentThread().name}")
                val bufferedSink = file.sink().buffer()
                body?.let { safeResponseBody ->
                    val safeSource = safeResponseBody.source()
                    bufferedSink.writeAll(safeSource)
                }
                bufferedSink.close()
                logDebug("Written to file with path -- ${file.path}")
            } catch (e: FileNotFoundException) {
                logException(e)
            } catch (e: IOException) {
                logException(e)
            }
        }
    }
}