package com.kunalkalra.downloadmanagercore.data.dao

import androidx.room.Dao

@Dao
interface DownloadDao {

    suspend fun insertDownload()

    suspend fun getDownload()

    suspend fun deleteDownload()
}