package com.kunalkalra.downloadmanagercore.downloadManager.exceptions

class MimeTypeNotDetermined(message: String? = null): Exception(message) {
    override val message: String
        get() = super.message ?: "Could not determine file type"
}