package com.kunalkalra.downloadmanager

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kunalkalra.downloadmanagercore.downloadManager.CoreDownloadManager
import com.kunalkalra.downloadmanagercore.downloadManager.models.CoreDownloadRequest


class MainActivity : AppCompatActivity() {

    private lateinit var coreDownloadManager: CoreDownloadManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val download = findViewById<TextView>(R.id.btnDownload)
        coreDownloadManager = CoreDownloadManager(applicationContext)
        download.setOnClickListener {
            coreDownloadManager.download(
                CoreDownloadRequest(
                    url = "https://filesamples.com/samples/video/mp4/sample_1920x1080.mp4",
                    fileName = "bizbizcat",
                )
            )
        }

    }
}