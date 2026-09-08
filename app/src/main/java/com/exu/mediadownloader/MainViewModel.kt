package com.exu.mediadownloader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DownloadUi(
    val url: String,
    val fileName: String = "download.bin",
    val progress: Int = 0,
    val status: String = "Ready",
    val speed: String = "0 B/s"
)

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val _sharedUrl = MutableStateFlow<String?>(null)
    val sharedUrl: StateFlow<String?> = _sharedUrl.asStateFlow()

    private val _downloads = MutableStateFlow<List<DownloadUi>>(emptyList())
    val downloads: StateFlow<List<DownloadUi>> = _downloads.asStateFlow()

    fun receiveSharedUrl(raw: String) {
        val url = raw.trim().split(Regex("\\s+")).firstOrNull { it.startsWith("http://") || it.startsWith("https://") }
        _sharedUrl.value = url
    }

    fun dismissShare() { _sharedUrl.value = null }

    fun startDirectDownload(url: String) {
        val name = url.substringAfterLast('/').substringBefore('?').ifBlank { "download.bin" }
        _downloads.value = listOf(
            DownloadUi(url = url, fileName = name, status = "Queued")
        ) + _downloads.value
        viewModelScope.launch {
            val index = 0
            _downloads.value = _downloads.value.toMutableList().also {
                it[index] = it[index].copy(status = "Direct downloads are ready to be wired to DownloadEngine.")
            }
        }
    }
}
