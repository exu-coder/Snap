package com.exu.mediadownloader.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Upsert suspend fun upsert(item: DownloadEntity)
    @Query("SELECT * FROM downloads ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<DownloadEntity>>
    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun delete(id: String)
}
