package com.kunalkalra.downloadmanagercore.fileIO

import com.kunalkalra.downloadmanagercore.utils.logDebug
import com.kunalkalra.downloadmanagercore.utils.logException
import okhttp3.ResponseBody
import okio.FileNotFoundException
import okio.IOException
import okio.buffer
import okio.sink
import java.io.File

class FileManager: IFileOperations {

    override fun createFile(path: String): File? {
        val file = File(path)
        return try {
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

    override fun writeToFile(file: File, body: ResponseBody?) {
        try {
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

    override fun writeToFileInChunks(file: File, body: ResponseBody?, chunkSize: Long) {
        TODO("Not yet implemented")
    }
}