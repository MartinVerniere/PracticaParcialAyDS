package ayds.apolo.songinfo.home.model.repository

import ayds.apolo.songinfo.home.model.entities.EmptySong
import ayds.apolo.songinfo.home.model.entities.SearchResult
import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.external.spotify.SpotifyTrackService
import ayds.apolo.songinfo.home.model.repository.external.wikipedia.WikipediaAPIService
import ayds.apolo.songinfo.home.model.repository.local.cache.SpotifyCache
import ayds.apolo.songinfo.home.model.repository.local.spotify.SpotifyLocalStorage

interface SongRepository {
    fun getSongByTerm(term: String): SearchResult
}

internal class SongRepositoryImpl(
    private val spotifyCache: SpotifyCache,
    private val spotifyLocalStorage: SpotifyLocalStorage,
    private val spotifyTrackService: SpotifyTrackService,
    private val spotifyWikipediaService: WikipediaAPIService
) : SongRepository {

    override fun getSongByTerm(term: String): SearchResult {
        var spotifySong = spotifyCache.searchSongInCache(term)
        when (spotifySong) {
            is SpotifySong -> markSongAsCacheStored(spotifySong)
            else -> {
                spotifySong = spotifyLocalStorage.getSongByTerm(term)
                when (spotifySong) {
                    is SpotifySong -> {
                        markSongAsLocallyStored(spotifySong)
                        spotifyCache.updateCacheWithSong(term, spotifySong)
                    }
                    else -> {
                        spotifySong = spotifyTrackService.getSong(term)
                        when (spotifySong) {
                            is SpotifySong -> spotifyLocalStorage.insertSong(term, spotifySong)
                            else -> spotifySong = spotifyWikipediaService.getSong(term)
                        }
                    }
                }
            }
        }

        return spotifySong ?: EmptySong
    }

    private fun markSongAsCacheStored(spotifySong: SpotifySong) {
        spotifySong.isCacheStored = true
    }

    private fun markSongAsLocallyStored(spotifySong: SpotifySong) {
        spotifySong.isLocallyStored = true
    }
}