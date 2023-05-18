package ayds.apolo.songinfo.home.model

import ayds.apolo.songinfo.home.model.entities.SearchResult
import ayds.apolo.songinfo.home.model.repository.SongRepository

interface HomeModel {

    fun searchSong(term: String): SearchResult
}

internal class HomeModelImpl(private val repository: SongRepository) : HomeModel {
    override fun searchSong(term: String) = repository.getSongByTerm(term)
}