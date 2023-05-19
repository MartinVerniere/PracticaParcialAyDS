package ayds.apolo.songinfo.home.model

import ayds.apolo.songinfo.home.model.repository.SongRepository
import ayds.apolo.songinfo.home.model.repository.SongRepositoryImpl

object HomeModelInjector {

  private val repository: SongRepository = SongRepositoryImpl()

  val homeModel: HomeModel = HomeModelImpl(repository)
}