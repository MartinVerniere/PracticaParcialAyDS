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


class SongRepository {

    internal val sls = SpotifySqlDBImpl(
        SpotifySqlQueriesImpl(), ResultSetToSpotifySongMapperImpl()
    )
    val sts = SpotifyModule.spotifyTrackService

    val theCache = mutableMapOf<String, SpotifySong>()

    ///// Wiki
    var retrofit: Retrofit? = Retrofit.Builder()
        .baseUrl("https://en.wikipedia.org/w/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    var wikipediaAPI = retrofit!!.create(WikipediaAPI::class.java)
    //// end wiki

    fun getSongByTerm(term: String): SearchResult {
        var s: SpotifySong?

        // check in the cache
        s = theCache[term]
        if (s != null) {
            s.isCacheStored = true
            return s
        }

        // check in the DB
        s = sls.getSongByTerm(term)
        if (s != null) {
            s.isLocallyStored = true
            // update the cache
            theCache[term] = s
            return s
        }

        // the service
        s = sts.getSong(term)
        if (s != null) {
            sls.insertSong(term, s)
            return s
        }

        /////// Last chance, get anything from the wiki
        val callResponse: Response<String>
        try {
            callResponse = wikipediaAPI.getInfo(term).execute()
            System.out.println("JSON " + callResponse.body())
            val gson = Gson()
            val jobj: JsonObject = gson.fromJson(callResponse.body(), JsonObject::class.java)
            val query = jobj["query"].asJsonObject
            val snippetObj = query["search"].asJsonArray.firstOrNull()
            if (snippetObj != null) {
                val snippet = snippetObj.asJsonObject["snippet"]
                return SpotifySong("", snippet.asString, " - ", " - ", " - ", "", "")
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return EmptySong
    }
}