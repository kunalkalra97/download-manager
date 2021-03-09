package com.kunalkalra.downloadmanagercore.usecases.fileIOUseCases

import com.kunalkalra.downloadmanagercore.fileIO.FileManager
import com.kunalkalra.downloadmanagercore.usecases.base.BasePerformUseCase

class UseCaseDeleteFile(private val fileManager: FileManager) : BasePerformUseCase<String, Boolean>() {

    override fun performOperation(param: String): Boolean {
        return fileManager.deleteFile(param)
    }
}