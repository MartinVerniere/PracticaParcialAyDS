package ayds.apolo.songinfo.home.model

import ayds.apolo.songinfo.home.model.repository.SongRepository

object HomeModelInjector {

  private val repository: SongRepository = SongRepository()

  val homeModel: HomeModel = HomeModelImpl(repository)
}