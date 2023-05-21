package ayds.apolo.songinfo.home.model.repository.external.wikipedia

import ayds.apolo.songinfo.home.model.repository.WikipediaAPI
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object SpotifyWikipediaModule {

    private const val WIKI_URL = "https://en.wikipedia.org/w/"
    private var retrofit = Retrofit.Builder()
        .baseUrl(WIKI_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    private var wikipediaAPI: WikipediaAPI = retrofit.create(WikipediaAPI::class.java)
    private var spotifyToSongResolverWiki: SpotifyToSongResolverWiki = JsonToSongResolverWiki()

    val wikipediaAPIService: WikipediaAPIService = WikipediaAPIServiceImpl(
        wikipediaAPI,
        spotifyToSongResolverWiki
    )
}