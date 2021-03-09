package com.kunalkalra.downloadmanagercore.usecases.base

/**
 * Base Use Case for suspending interaction between service and utility classes
 * @param Param Input Type
 * @param Result Output Type
 */

abstract class BaseSuspendPrepareAndPerformUseCase<in PrepareParams, in Param, out Result>: BaseSuspendPerformUseCase<Param, Result>() {
    abstract suspend fun prepare(prepareParams: PrepareParams)
}