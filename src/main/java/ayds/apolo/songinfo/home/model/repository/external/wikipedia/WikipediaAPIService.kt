package ayds.apolo.songinfo.home.model.repository.external.wikipedia

import ayds.apolo.songinfo.home.model.entities.SpotifySong

interface WikipediaAPIService {

    fun getSong(term: String): SpotifySong?
}