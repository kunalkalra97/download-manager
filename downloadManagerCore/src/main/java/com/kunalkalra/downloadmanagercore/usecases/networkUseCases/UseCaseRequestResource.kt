package com.kunalkalra.downloadmanagercore.usecases.networkUseCases

import com.kunalkalra.downloadmanagercore.network.INetworkOperations
import com.kunalkalra.downloadmanagercore.network.models.SafeResult
import com.kunalkalra.downloadmanagercore.usecases.base.BaseSuspendPerformUseCase
import okhttp3.Request
import okhttp3.Response

class UseCaseRequestResource(
    private val networkManager: INetworkOperations<Request, Response>
): BaseSuspendPerformUseCase<Request, Response?>() {

    override suspend fun performOperation(param: Request): Response? {
        return when(val response = networkManager.requestResource(request = param)) {
            is SafeResult.Success -> {
                response.data
            }

            is SafeResult.Failure -> {
                throw response.throwable
            }
        }
    }
}