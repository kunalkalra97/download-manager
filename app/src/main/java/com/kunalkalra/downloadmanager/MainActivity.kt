package com.kunalkalra.downloadmanager

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kunalkalra.downloadmanagercore.CoreDownloadManager
import com.kunalkalra.downloadmanagercore.CoreDownloadRequest
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private val url = "https://asia.olympus-imaging.com/content/000107506.jpg"
    private var id: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val download = findViewById<TextView>(R.id.btnDownload)
        val downloadManager = CoreDownloadManager(applicationContext)

        download.setOnClickListener {
            downloadManager.download(CoreDownloadRequest(
                url = url,
                fileName = "something"
            ))
        }

    }
}