package com.kunalkalra.downloadmanagercore.usecases.fileIOUseCases

import com.kunalkalra.downloadmanagercore.downloadManager.exceptions.FileExistsException
import com.kunalkalra.downloadmanagercore.fileIO.FileManager
import com.kunalkalra.downloadmanagercore.usecases.base.BaseSuspendPerformUseCase
import okhttp3.ResponseBody
import java.io.File
import kotlin.jvm.Throws

class UseCaseWriteToExistingFileInChunks(private val fileManager: FileManager) :
    BaseSuspendPerformUseCase<UseCaseWriteToExistingFileInChunks.FileParams, Unit>() {

    companion object {
        fun getFileWriteParams(
            filePath: String,
            body: ResponseBody?,
            chunkSize: Long,
            seek: Long
        ): FileParams =
            FileParams(chunkSize, seek, filePath, body)
    }

    /**
     * Operation to write to a file already existing. If file exists, throw [FileExistsException]
     * Create a new file and write the response body
     * @param param [FileParams] containing `filePath`[String] and `responseBody`[ResponseBody]
     */

    override suspend fun performOperation(param: FileParams) {
        val filePath = param.filePath
        val body = param.body
        val chunkSize = param.chunkSize
        val seek = param.seek
        when (fileManager.doesFileExist(filePath)) {
            true -> {
                val file = File(filePath)
                fileManager.writeToFileInChunksWithSeek(file, body, chunkSize, seek)
            }
            else -> {
            }
        }
    }

    data class FileParams(
        val chunkSize: Long,
        val seek: Long,
        val filePath: String,
        val body: ResponseBody?
    )
}