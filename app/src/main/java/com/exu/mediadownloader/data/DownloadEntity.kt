package com.exu.mediadownloader.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String,
    val url: String,
    val fileName: String,
    val status: String,
    val progress: Int,
    val createdAt: Long
)
