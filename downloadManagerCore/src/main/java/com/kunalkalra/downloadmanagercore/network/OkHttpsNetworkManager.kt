package com.kunalkalra.downloadmanagercore.network

import com.kunalkalra.downloadmanagercore.network.models.SafeResult
import com.kunalkalra.downloadmanagercore.utils.logDebug
import com.kunalkalra.downloadmanagercore.utils.logException
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

class OkHttpsNetworkManager: INetworkOperations<Request, Response> {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient()
    }

    override suspend fun requestResource(coroutineContext: CoroutineContext, request: Request): SafeResult<Response> {
        return withContext(coroutineContext) {
            suspendCancellableCoroutine<SafeResult<Response>> { cancellableContinuation ->
                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        logException(e)
                        cancellableContinuation.resume(SafeResult.Failure(e, e.message))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        cancellableContinuation.resume(SafeResult.Success(response))
                    }

                })
            }
        }
    }
}