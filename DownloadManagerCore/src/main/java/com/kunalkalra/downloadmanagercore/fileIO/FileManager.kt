package com.kunalkalra.downloadmanagercore.fileIO

import com.kunalkalra.downloadmanagercore.utils.logException
import com.kunalkalra.downloadmanagercore.utils.withIOContext
import okhttp3.ResponseBody
import okio.FileNotFoundException
import okio.IOException
import okio.buffer
import okio.sink
import java.io.File

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
            }
        }
    }
}