package ayds.apolo.songinfo.home.model.repository

import ayds.apolo.songinfo.home.model.entities.EmptySong
import ayds.apolo.songinfo.home.model.entities.SearchResult
import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.external.spotify.SpotifyModule
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.ResultSetToSpotifySongMapperImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlDBImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlQueriesImpl
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException


private const val WIKI_URL = "https://en.wikipedia.org/w/"
private const val JSON = "JSON"
private const val QUERY = "query"
private const val SEARCH = "search"
private const val SNIPPET = "snippet"

class SongRepository {

    internal val spotifyLocalStorage = SpotifySqlDBImpl(
        SpotifySqlQueriesImpl(), ResultSetToSpotifySongMapperImpl()
    )
    val spotifyTrackService = SpotifyModule.spotifyTrackService

    val spotifyCache = mutableMapOf<String, SpotifySong>()

    ///// Wiki
    var retrofit: Retrofit? = Retrofit.Builder()
        .baseUrl(WIKI_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    var wikipediaAPI = retrofit!!.create(WikipediaAPI::class.java)
    //// end wiki

    fun getSongByTerm(term: String): SearchResult {
        // check in the cache
        var spotifySong = searchSongInCache(term)
        when (spotifySong){
            is SpotifySong -> markSongAsCacheStored(spotifySong)
            else -> { // check in the DB
                spotifySong = searchSongInLocalStorage(term)
                when (spotifySong){
                    is SpotifySong -> {
                        markSongAsLocallyStored(spotifySong)
                        // update the cache
                        updateCacheWithSong(term,spotifySong)
                    }
                    else -> {  // the service
                        spotifySong = searchSongInSongRepository(term)
                        when (spotifySong){ /////// Last chance, get anything from the wiki
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

    private fun searchSongInSongRepository(term: String) = spotifyTrackService.getSong(term)

    private fun getSongFromWikipedia(term: String): SpotifySong? {
        val callResponse: Response<String>
        try {
            callResponse = wikipediaAPI.getInfo(term).execute()
            System.out.println(JSON + callResponse.body())
            val gson = Gson()
            val jobj: JsonObject =
                gson.fromJson(callResponse.body(), JsonObject::class.java)
            val query = jobj[QUERY].asJsonObject
            val snippetObj = query[SEARCH].asJsonArray.firstOrNull()
            if (snippetObj != null) {
                val snippet = snippetObj.asJsonObject[SNIPPET]
                return SpotifySong("", snippet.asString, " - ", " - ", " - ", "", "")
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return null
    }
}