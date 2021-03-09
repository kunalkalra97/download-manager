package com.kunalkalra.downloadmanagercore.downloadManager.exceptions

class MimeTypeNotDeterminedException(message: String? = null): Exception(message) {
    override val message: String
        get() = super.message ?: "Could not determine file type"
}