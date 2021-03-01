package com.kunalkalra.downloadmanager

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kunalkalra.downloadmanagercore.downloadManager.CoreDownloadManager
import com.kunalkalra.downloadmanagercore.downloadManager.models.CoreDownloadRequest


class MainActivity : AppCompatActivity() {

    private lateinit var coreDownloadManager: CoreDownloadManager
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val download = findViewById<TextView>(R.id.btnDownload)
        coreDownloadManager = CoreDownloadManager(applicationContext)
        val randomString = (1..6)
                .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");

        download.setOnClickListener {
            coreDownloadManager.download(
                CoreDownloadRequest(
                    url = "https://file-examples-com.github.io/uploads/2017/10/file_example_JPG_2500kB.jpg",
                    fileName = randomString,
                )
            )
        }
    }
}