package com.kunalkalra.downloadmanagercore.network

import com.kunalkalra.downloadmanagercore.network.models.SafeResult
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 * Defines a contract for requesting a remote resource
 * @author Kunal Kalra
 * @param <S> Request type
 * @param <T> Response type
 */

interface INetworkOperations<in S, out T> {
    /**
     * Method for requesting a remote Resource
     * @param coroutineContext of the task
     * @param request Request Type
     * @return Response Type <T> wrapped in a [SafeResult]
     */
    suspend fun requestResource(coroutineContext: CoroutineContext = Dispatchers.IO, request: S): SafeResult<T>
}