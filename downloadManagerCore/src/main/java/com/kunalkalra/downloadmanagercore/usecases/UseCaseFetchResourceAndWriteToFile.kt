package com.kunalkalra.downloadmanagercore.usecases

import com.kunalkalra.downloadmanagercore.downloadManager.exceptions.MimeTypeNotDeterminedException
import com.kunalkalra.downloadmanagercore.downloadManager.models.CoreDownloadRequest
import com.kunalkalra.downloadmanagercore.usecases.base.BaseSuspendPerformUseCase
import com.kunalkalra.downloadmanagercore.usecases.base.BaseSuspendPrepareAndPerformUseCase
import com.kunalkalra.downloadmanagercore.usecases.fileIOUseCases.UseCaseWriteToFile
import com.kunalkalra.downloadmanagercore.usecases.networkUseCases.UseCaseRequestResource
import com.kunalkalra.downloadmanagercore.utils.MimeUtils
import okhttp3.Request
import okhttp3.Response

class UseCaseFetchResourceAndWriteToFile(
    private val useCaseWriteToFile: UseCaseWriteToFile,
    private val useCaseRequestResource: UseCaseRequestResource
): BaseSuspendPrepareAndPerformUseCase<CoreDownloadRequest, Request, Unit>() {

    lateinit var downloadRequest: CoreDownloadRequest

    override suspend fun prepare(prepareParams: CoreDownloadRequest) {
        downloadRequest = prepareParams
    }

    @Throws(MimeTypeNotDeterminedException::class)
    override suspend fun performOperation(param: Request) {
        val response = useCaseRequestResource.performOperation(param)
        response?.let { safeResponse ->
            val mimeType = determineMimeType(safeResponse)
            val completePath = downloadRequest.getCompleteFilePathWithMimeType(mimeType)
            useCaseWriteToFile.performOperation(
                UseCaseWriteToFile.getFileWriteParams(
                    completePath,
                    response.body
                )
            )
        }
    }

    private fun determineMimeType(response: Response): String {
        val mimeType = MimeUtils.getExtensionFromResponse(response)
        mimeType?.let {
            return it
        }?: kotlin.run {
            throw MimeTypeNotDeterminedException()
        }
    }
}