package ayds.apolo.songinfo.home.model.repository

import ayds.apolo.songinfo.home.model.entities.EmptySong
import ayds.apolo.songinfo.home.model.entities.SearchResult
import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.external.spotify.SpotifyTrackService
import ayds.apolo.songinfo.home.model.repository.local.cache.SpotifyCache
import ayds.apolo.songinfo.home.model.repository.local.spotify.SpotifyLocalStorage
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException


private const val WIKI_URL = "https://en.wikipedia.org/w/"
private const val QUERY = "query"
private const val SEARCH = "search"
private const val SNIPPET = "snippet"

interface SongRepository {
    fun getSongByTerm(term: String): SearchResult
}

internal class SongRepositoryImpl(
    private val spotifyCache: SpotifyCache,
    private val spotifyLocalStorage: SpotifyLocalStorage,
    private val spotifyTrackService: SpotifyTrackService
) : SongRepository {

    private var retrofit: Retrofit = initRetrofit()
    private var wikipediaAPI: WikipediaAPI = initWikipediaAPI(retrofit)

    override fun getSongByTerm(term: String): SearchResult {
        var spotifySong = spotifyCache.searchSongInCache(term)
        when (spotifySong) {
            is SpotifySong -> markSongAsCacheStored(spotifySong)
            else -> {
                spotifySong = searchSongInLocalStorage(term)
                when (spotifySong) {
                    is SpotifySong -> {
                        markSongAsLocallyStored(spotifySong)
                        spotifyCache.updateCacheWithSong(term, spotifySong)
                    }
                    else -> {
                        spotifySong = searchSongInTrackService(term)
                        when (spotifySong) {
                            is SpotifySong -> spotifyLocalStorage.insertSong(term, spotifySong)
                            else -> spotifySong = getSongFromWikipedia(term)
                        }
                    }
                }
            }
        }

        return spotifySong ?: EmptySong
    }

    private fun initRetrofit() = Retrofit.Builder()
        .baseUrl(WIKI_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    private fun initWikipediaAPI(retrofit: Retrofit) = retrofit.create(WikipediaAPI::class.java)

    private fun markSongAsCacheStored(spotifySong: SpotifySong) { spotifySong.isCacheStored = true }

    private fun searchSongInLocalStorage(term: String) = spotifyLocalStorage.getSongByTerm(term)

    private fun markSongAsLocallyStored(spotifySong: SpotifySong) { spotifySong.isLocallyStored = true }

    private fun searchSongInTrackService(term: String) = spotifyTrackService.getSong(term)

    private fun getSongFromWikipedia(term: String): SpotifySong? {
        try {
            val callResponse = getCallResponse(term)
            val snippetObj = getSnippetObject(callResponse)
            if (snippetObj != null) {
                val snippet = snippetObj.getSnippet()
                return SpotifySong("", snippet.asString, " - ", " - ", " - ", "", "")
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return null
    }

    private fun getCallResponse(term: String) = wikipediaAPI.getInfo(term).execute()

    private fun getSnippetObject(callResponse: Response<String>): JsonElement? {
        val gson = Gson()
        val jObj: JsonObject = gson.fromJson(callResponse.body(), JsonObject::class.java)
        val query = jObj.getQuery()
        return query.getSearch()
    }

    private fun JsonElement.getSnippet() = this.asJsonObject[SNIPPET]
    private fun JsonObject.getQuery() = this[QUERY].asJsonObject
    private fun JsonObject.getSearch() = this[SEARCH].asJsonArray.firstOrNull()
}