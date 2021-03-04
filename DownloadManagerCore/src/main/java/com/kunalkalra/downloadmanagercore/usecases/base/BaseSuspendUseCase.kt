package com.kunalkalra.downloadmanagercore.usecases.base

/**
 * Base Use Case for suspending interaction between service and utility classes
 * @param Param Input Type
 * @param Result Output Type
 */

abstract class BaseSuspendUseCase<in Param, out Result> {
    abstract suspend fun performOperation(param: Param): Result
}