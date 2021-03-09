package com.kunalkalra.downloadmanagercore.downloadManager.exceptions

class FileExistsException(message: String? = null): Exception(message) {
    override val message: String?
        get() = super.message?: "File Already Exists"
}