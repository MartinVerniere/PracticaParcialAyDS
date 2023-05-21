package ayds.apolo.songinfo.home.model.repository.external.spotify

import ayds.apolo.songinfo.home.model.repository.external.spotify.tracks.*
import ayds.apolo.songinfo.home.model.repository.external.wikipedia.SpotifyWikipediaModule
import ayds.apolo.songinfo.home.model.repository.external.wikipedia.WikipediaAPIService

object SpotifyModule {

  val spotifyTrackService: SpotifyTrackService = SpotifyTrackModule.spotifyTrackService
  val spotifyWikipediaAPIService: WikipediaAPIService = SpotifyWikipediaModule.wikipediaAPIService
}