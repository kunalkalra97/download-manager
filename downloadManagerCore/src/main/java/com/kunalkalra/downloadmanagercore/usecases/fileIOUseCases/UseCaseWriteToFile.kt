package com.kunalkalra.downloadmanagercore.usecases.fileIOUseCases

import com.kunalkalra.downloadmanagercore.downloadManager.exceptions.FileExistsException
import com.kunalkalra.downloadmanagercore.fileIO.FileManager
import com.kunalkalra.downloadmanagercore.fileIO.IFileOperations
import com.kunalkalra.downloadmanagercore.usecases.base.BaseSuspendPerformUseCase
import okhttp3.ResponseBody
import java.io.File
import kotlin.jvm.Throws

/**
 * Use case for writing [ResponseBody] to a [File]
 * @param fileManager [IFileOperations] which handles file operations implemented in [FileManager]
 */

class UseCaseWriteToFile(private val fileManager: IFileOperations) :
    BaseSuspendPerformUseCase<UseCaseWriteToFile.FileParams, Unit>() {

    companion object {
        fun getFileWriteParams(filePath: String, body: ResponseBody?): FileParams =
            FileParams(filePath, body)
    }

    /**
     * Operation to write to a file. If file exists, throw [FileExistsException]
     * Create a new file and write the response body
     * @param param [FileParams] containing `filePath`[String] and `responseBody`[ResponseBody]
     */

    @Throws(FileExistsException::class)
    override suspend fun performOperation(param: FileParams) {
        val filePath = param.filePath
        val body = param.body
        when(fileManager.doesFileExist(filePath)) {
            true -> {
                throw FileExistsException()
            }
            else -> {
                val file = fileManager.createFile(filePath)
                file?.let {
                    fileManager.writeToFile(file, body)
                }
            }
        }
    }

    data class FileParams(
        val filePath: String,
        val body: ResponseBody?
    )
}