package ayds.apolo.songinfo.home.model.repository.external.wikipedia

import ayds.apolo.songinfo.home.model.entities.SpotifySong
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.IOException

interface SpotifyToSongResolverWiki {

    fun getSongFromWikipedia(serviceData: String?): SpotifySong?
}

private const val QUERY = "query"
private const val SEARCH = "search"
private const val SNIPPET = "snippet"

internal class JsonToSongResolverWiki: SpotifyToSongResolverWiki {

    override fun getSongFromWikipedia(serviceData: String?): SpotifySong? =
        try {
            serviceData?.getFirstItem()?.let { item ->
                SpotifySong(
                    "", item.getSnippet(), " - ", " - ", " - ", "", ""
                )
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
            null
        }

    private fun String?.getFirstItem(): JsonObject{
        val jObj = Gson().fromJson(this, JsonObject::class.java)
        val query = jObj[QUERY].asJsonObject
        val search = query[SEARCH].asJsonArray
        return search[0].asJsonObject
    }

    private fun JsonObject.getSnippet() = this[SNIPPET].asString
}