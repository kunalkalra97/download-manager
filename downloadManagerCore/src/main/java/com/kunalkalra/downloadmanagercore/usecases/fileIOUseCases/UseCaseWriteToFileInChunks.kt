package com.kunalkalra.downloadmanagercore.usecases.fileIOUseCases

import com.kunalkalra.downloadmanagercore.downloadManager.exceptions.FileExistsException
import com.kunalkalra.downloadmanagercore.fileIO.FileManager
import com.kunalkalra.downloadmanagercore.usecases.base.BaseSuspendPerformUseCase
import okhttp3.ResponseBody
import kotlin.jvm.Throws

class UseCaseWriteToFileInChunks(
    private val fileManager: FileManager
): BaseSuspendPerformUseCase<UseCaseWriteToFileInChunks.FileParams, Unit>() {

    companion object {
        fun getFileWriteInChunksParams(filePath: String, body: ResponseBody?, chunkSize: Long): FileParams =
            FileParams(chunkSize, filePath, body)
    }

    /**
     * Operation to write to a file in chunks. If file exists, throw [FileExistsException]
     * Create a new file and write the response body
     * @param param [FileParams] containing `filePath`[String] and `responseBody`[ResponseBody]
     */

    @Throws(FileExistsException::class)
    override suspend fun performOperation(param: FileParams) {
        val filePath = param.filePath
        val body = param.body
        val chunkSize = param.chunkSize
        when (fileManager.doesFileExist(filePath)) {
            true -> {
                throw FileExistsException()
            }
            else -> {
                val file = fileManager.createFile(filePath)
                file?.let {
                    fileManager.writeToFileInChunks(file, body, chunkSize)
                }
            }
        }
    }

    data class FileParams(
        val chunkSize: Long,
        val filePath: String,
        val body: ResponseBody?
    )
}