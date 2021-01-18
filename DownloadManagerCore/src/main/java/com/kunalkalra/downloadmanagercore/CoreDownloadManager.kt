package com.kunalkalra.downloadmanagercore

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

typealias AndroidDownloadManager = DownloadManager

class CoreDownloadManager(
    private val applicationContext: Context
): IDownloadManger {

    private var isReceiverRegistered = false
    private val coreDownloadBroadcastReceiver by lazy {
        CoreDownloadBroadcastReceiver()
    }
    private val androidDownloadManager by lazy {
        applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as AndroidDownloadManager
    }

    override val permissions: Array<String>
        get() = arrayOf(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun download(coreDownloadRequest: CoreDownloadRequest, cookie: String) {
        val androidDownloadManagerRequest = coreDownloadRequest.toAndroidDownloadRequest(cookie)
        registerBroadcastReceivers()
        androidDownloadManager.enqueue(androidDownloadManagerRequest)
    }

    private fun registerBroadcastReceivers() {
        val downloadIntentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        applicationContext.registerReceiver(coreDownloadBroadcastReceiver, downloadIntentFilter)
        isReceiverRegistered = true
    }

    /**
     * Unregisters receiver that listens for Download Action broadcast.
     * Should be called when lifecycle of component using it comes to an end
     */
    private fun unregisterBroadCastReceivers() {
        if(isReceiverRegistered) {
            applicationContext.unregisterReceiver(coreDownloadBroadcastReceiver)
            isReceiverRegistered = false
        }
    }

    inner class CoreDownloadBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val broadcastDownloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            // Use this broadcast ID to make checks
        }
    }


}
