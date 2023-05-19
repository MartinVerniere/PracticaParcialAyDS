package ayds.apolo.songinfo.home.model.repository

import ayds.apolo.songinfo.home.model.entities.EmptySong
import ayds.apolo.songinfo.home.model.entities.SearchResult
import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.external.spotify.SpotifyModule
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.ResultSetToSpotifySongMapperImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlDBImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlQueriesImpl
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

class SongRepository {

    private val spotifyLocalStorage = SpotifySqlDBImpl(
        SpotifySqlQueriesImpl(), ResultSetToSpotifySongMapperImpl()
    )
    private val spotifyTrackService = SpotifyModule.spotifyTrackService

    private val spotifyCache = mutableMapOf<String, SpotifySong>()

    private var retrofit: Retrofit? = Retrofit.Builder()
        .baseUrl(WIKI_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private var wikipediaAPI: WikipediaAPI = retrofit!!.create(WikipediaAPI::class.java)

    fun getSongByTerm(term: String): SearchResult {
        var spotifySong = searchSongInCache(term)
        when (spotifySong) {
            is SpotifySong -> markSongAsCacheStored(spotifySong)
            else -> {
                spotifySong = searchSongInLocalStorage(term)
                when (spotifySong){
                    is SpotifySong -> {
                        markSongAsLocallyStored(spotifySong)
                        updateCacheWithSong(term,spotifySong)
                    }
                    else -> {
                        spotifySong = searchSongInTrackService(term)
                        when (spotifySong){
                            is SpotifySong -> spotifyLocalStorage.insertSong(term, spotifySong)
                            else -> spotifySong=getSongFromWikipedia(term)
                        }
                    }
                }
            }
        }

        return spotifySong ?: EmptySong
    }

    private fun searchSongInCache(term: String) = spotifyCache[term]

    private fun markSongAsCacheStored(spotifySong: SpotifySong) { spotifySong.isCacheStored = true }

    private fun updateCacheWithSong(term: String, spotifySong: SpotifySong){ spotifyCache[term] = spotifySong }

    private fun searchSongInLocalStorage(term: String) = spotifyLocalStorage.getSongByTerm(term)

    private fun markSongAsLocallyStored(spotifySong: SpotifySong) { spotifySong.isLocallyStored = true }

    private fun searchSongInTrackService(term: String) = spotifyTrackService.getSong(term)

    private fun getSongFromWikipedia(term: String): SpotifySong? {
        val callResponse: Response<String>
        try {
            callResponse = wikipediaAPI.getInfo(term).execute()
            val snippetObj = getSnippetObject(callResponse)
            if (snippetObj != null) {
                val snippet = snippetObj.asJsonObject[SNIPPET]
                return SpotifySong("", snippet.asString, " - ", " - ", " - ", "", "")
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return null
    }

    private fun getSnippetObject(callResponse: Response<String>): JsonElement? {
        val gson = Gson()
        val jObj: JsonObject = gson.fromJson(callResponse.body(), JsonObject::class.java)
        val query = jObj[QUERY].asJsonObject
        return query[SEARCH].asJsonArray.firstOrNull()
    }
}