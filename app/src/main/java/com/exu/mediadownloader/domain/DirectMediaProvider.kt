package com.exu.mediadownloader.domain

import okhttp3.OkHttpClient
import okhttp3.Request

class DirectMediaProvider(
    private val client: OkHttpClient = OkHttpClient()
) : MediaProvider {
    override fun canHandle(url: String): Boolean =
        url.startsWith("https://") && listOf(".mp4", ".webm", ".m4a", ".mp3", ".ogg", ".wav", ".png", ".jpg", ".jpeg", ".webp")
            .any { url.substringBefore("?").lowercase().endsWith(it) }

    override suspend fun resolve(url: String): Result<MediaInfo> = runCatching {
        require(canHandle(url)) { "Not a recognized direct media URL." }
        val name = url.substringAfterLast('/').substringBefore('?').ifBlank { "media" }
        MediaInfo(
            title = name,
            formats = listOf(MediaFormat("original", "Original", "application/octet-stream", null, url)),
            source = url
        )
    }
}
