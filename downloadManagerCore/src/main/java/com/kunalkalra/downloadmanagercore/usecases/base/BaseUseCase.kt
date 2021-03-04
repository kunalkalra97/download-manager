package com.kunalkalra.downloadmanagercore.usecases.base

/**
 * Base Use Case for interaction between service and utility classes
 * @param Param Input Type
 * @param Result Output Type
 */

abstract class BaseUseCase<in Param, out Result> {
    abstract fun performOperation(param: Param): Result
}