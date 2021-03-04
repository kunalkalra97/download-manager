package com.kunalkalra.downloadmanagercore.usecases.fileIOUseCases

import com.kunalkalra.downloadmanagercore.fileIO.FileManager
import com.kunalkalra.downloadmanagercore.usecases.base.BaseUseCase

class UseCaseDeleteFile(private val fileManager: FileManager) : BaseUseCase<String, Boolean>() {

    override fun performOperation(param: String): Boolean {
        return fileManager.deleteFile(param)
    }
}