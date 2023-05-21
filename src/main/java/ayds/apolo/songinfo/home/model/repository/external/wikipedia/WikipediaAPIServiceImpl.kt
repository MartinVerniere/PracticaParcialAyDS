package ayds.apolo.songinfo.home.model.repository.external.wikipedia

import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.WikipediaAPI


internal class WikipediaAPIServiceImpl(
    private val wikipediaAPI: WikipediaAPI,
    private val spotifyToSongResolverWiki: SpotifyToSongResolverWiki
): WikipediaAPIService {

    override fun getSong(term: String): SpotifySong? {
        val callResponse = getCallResponse(term)
        return spotifyToSongResolverWiki.getSongFromWikipedia(callResponse.body())
    }

    private fun getCallResponse(term: String) = wikipediaAPI.getInfo(term).execute()
}