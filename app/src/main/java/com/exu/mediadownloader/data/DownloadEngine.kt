package com.exu.mediadownloader.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/**
 * Safe direct-resource downloader foundation.
 * Callers should pass a URL that they are authorized to download.
 */
class DownloadEngine(
    private val client: OkHttpClient = OkHttpClient()
) {
    suspend fun download(url: String, destination: File, onProgress: (Long, Long?) -> Unit = { _, _ -> }) =
        withContext(Dispatchers.IO) {
            require(url.startsWith("https://")) { "Only HTTPS downloads are allowed." }
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                check(response.isSuccessful) { "Server returned ${response.code}" }
                val body = response.body ?: error("Empty response")
                val total = body.contentLength().takeIf { it >= 0 }
                destination.parentFile?.mkdirs()
                body.byteStream().use { input ->
                    destination.outputStream().use { output ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var downloaded = 0L
                        while (true) {
                            val read = input.read(buffer)
                            if (read < 0) break
                            output.write(buffer, 0, read)
                            downloaded += read
                            onProgress(downloaded, total)
                        }
                    }
                }
            }
            destination
        }
}
