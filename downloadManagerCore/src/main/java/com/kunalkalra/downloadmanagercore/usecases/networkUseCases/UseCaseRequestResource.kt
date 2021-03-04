package com.kunalkalra.downloadmanagercore.usecases.networkUseCases

import com.kunalkalra.downloadmanagercore.network.INetworkOperations
import com.kunalkalra.downloadmanagercore.network.OkHttpsNetworkManager
import com.kunalkalra.downloadmanagercore.network.models.SafeResult
import com.kunalkalra.downloadmanagercore.usecases.base.BaseSuspendUseCase
import com.kunalkalra.downloadmanagercore.utils.logDebug
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class UseCaseRequestResource(
    private val networkManager: INetworkOperations<Request, Response>
): BaseSuspendUseCase<Request, Response?>() {

    override suspend fun performOperation(param: Request): Response? {
        return when(val response = networkManager.requestResource(request = param)) {
            is SafeResult.Success -> {
                response.data
            }

            is SafeResult.Failure -> {
                null
            }
        }
    }
}