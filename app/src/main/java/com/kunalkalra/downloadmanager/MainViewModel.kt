package com.kunalkalra.downloadmanager

import android.os.Environment
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import okhttp3.*
import okio.BufferedSink
import okio.Okio
import okio.buffer
import okio.sink
import timber.log.Timber
import java.io.File
import java.io.IOException


class MainViewModel : ViewModel() {

    private val url = "https://asia.olympus-imaging.com/content/000107506.jpg"
    private val viewUrl = "https://filesamples.com/samples/video/mp4/sample_3840x2160.mp4"

}