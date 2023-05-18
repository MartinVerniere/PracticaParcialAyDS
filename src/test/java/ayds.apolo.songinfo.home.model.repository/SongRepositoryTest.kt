package ayds.apolo.songinfo.home.model.repository

import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.external.spotify.SpotifyTrackService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test


class SongRepositoryTest {

    private val songRepository by lazy {
        SongRepository()
    }

    @Test
    fun `tests references`() {

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
}