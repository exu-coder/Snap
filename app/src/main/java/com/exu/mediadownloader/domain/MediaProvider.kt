package com.exu.mediadownloader.domain

data class MediaFormat(
    val id: String,
    val label: String,
    val mimeType: String,
    val sizeBytes: Long? = null,
    val url: String
)

data class MediaInfo(
    val title: String,
    val thumbnail: String? = null,
    val durationMs: Long? = null,
    val formats: List<MediaFormat>,
    val source: String,
)

interface MediaProvider {
    fun canHandle(url: String): Boolean
    suspend fun resolve(url: String): Result<MediaInfo>
}
