package ayds.apolo.songinfo.home.model.repository.local.cache

import ayds.apolo.songinfo.home.model.entities.SpotifySong

interface SpotifyCache {

    fun searchSongInCache(term: String): SpotifySong?

    fun updateCacheWithSong(term: String, spotifySong: SpotifySong)
}

internal class SpotifyCacheImpl : SpotifyCache {

    private val cacheStorage = mutableMapOf<String, SpotifySong>()

    override fun searchSongInCache(term: String): SpotifySong? = cacheStorage[term]

    override fun updateCacheWithSong(term: String, spotifySong: SpotifySong) {
        cacheStorage[term] = spotifySong
    }
}