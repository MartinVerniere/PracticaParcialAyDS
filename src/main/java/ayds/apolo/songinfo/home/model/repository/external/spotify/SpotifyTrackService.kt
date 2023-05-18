package ayds.apolo.songinfo.home.model.repository.external.spotify

import ayds.apolo.songinfo.home.model.entities.SpotifySong

interface SpotifyTrackService {

    fun getSong(title: String): SpotifySong?
}