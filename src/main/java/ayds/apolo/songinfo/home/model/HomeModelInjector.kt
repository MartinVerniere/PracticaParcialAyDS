package ayds.apolo.songinfo.home.model

import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.SongRepository
import ayds.apolo.songinfo.home.model.repository.SongRepositoryImpl
import ayds.apolo.songinfo.home.model.repository.external.spotify.SpotifyModule
import ayds.apolo.songinfo.home.model.repository.local.cache.SpotifyCacheImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.ResultSetToSpotifySongMapperImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlDBImpl
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlQueriesImpl

object HomeModelInjector {

  private val spotifyCache = SpotifyCacheImpl()
  private val spotifyLocalStorage = SpotifySqlDBImpl(
    SpotifySqlQueriesImpl(), ResultSetToSpotifySongMapperImpl()
  )
  private val spotifyTrackService = SpotifyModule.spotifyTrackService

  private val repository: SongRepository = SongRepositoryImpl(spotifyCache,spotifyLocalStorage,spotifyTrackService)

  val homeModel: HomeModel = HomeModelImpl(repository)

}

