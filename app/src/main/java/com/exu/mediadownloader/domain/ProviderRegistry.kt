package com.exu.mediadownloader.domain

class ProviderRegistry(private val providers: List<MediaProvider>) {
    fun providerFor(url: String): MediaProvider? = providers.firstOrNull { it.canHandle(url) }
}
