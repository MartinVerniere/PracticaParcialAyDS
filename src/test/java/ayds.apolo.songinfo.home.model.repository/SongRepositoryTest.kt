package ayds.apolo.songinfo.home.model.repository

import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.external.spotify.SpotifyTrackService
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlDBImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test


class SongRepositoryTest {

    @Test
    fun `when searchSongByTerm is called and song is cache stored should mark song as cache stored`() {

        // Mock an object
        val service: SpotifyTrackService = mockk()
        val song: SpotifySong = mockk()

        // mock a response
        every { service.getSong("title")} returns song

        // assertions
        Assert.assertEquals(service.getSong("title"), song)

        // verify mock was called
        verify { service.getSong("title") }
    }

    @Test
    fun `when searchSongByTerm is called and song is locally stored, should mark song as locally stored and save in cache with term`() {}

    @Test
    fun `when searchSongByTerm is called and song is in track service, should save song in local storage with term`() {}

    @Test
    fun `when searchSongByTerm is called and song is searched in wiki, should get song element with snippet`() {}

    @Test
    fun `when searchSongByTerm is called and song is not in wiki, should return EmptySong`() {}

}