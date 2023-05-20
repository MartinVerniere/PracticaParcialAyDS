package ayds.apolo.songinfo.home.model.repository

import ayds.apolo.songinfo.home.model.entities.EmptySong
import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.external.spotify.SpotifyTrackService
import ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb.SpotifySqlDBImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class SongRepositoryTest {

    //private val spotifyCache: Map<String, SpotifySong> = mockk(relaxUnitFun = true)
    private val spotifyLocalStorage: SpotifySqlDBImpl = mockk(relaxUnitFun = true)
    private val spotifyTrackService: SpotifyTrackService = mockk(relaxUnitFun = true)

    private val songRepository: SongRepository by lazy {
        SongRepositoryImpl(spotifyLocalStorage, spotifyTrackService)
    }

    @Test
    fun `when searchSongByTerm is called and song is in cache, should return song and mark song as cache stored`() {
        val song = SpotifySong(
            "songId",
            "songName",
            "artistName",
            "albumName",
            "releaseDate",
            "spotifyURL",
            "imageURL",
            isLocallyStored = false,
            isCacheStored = false
        )
        val expected = SpotifySong(
            "songId",
            "songName",
            "artistName",
            "albumName",
            "releaseDate",
            "spotifyURL",
            "imageURL",
            isLocallyStored = false,
            isCacheStored = true
        )
        //every { spotifyCache.getSongByTerm("term") } returns song

        val result = songRepository.getSongByTerm("term")

        assertEquals(result, expected)
        assertTrue(song.isCacheStored)
    }

    @Test
    fun `when searchSongByTerm is called and song is locally stored, should return song, mark song as locally stored and save in cache with term`() {
        val song = SpotifySong(
            "songId",
            "songName",
            "artistName",
            "albumName",
            "releaseDate",
            "spotifyURL",
            "imageURL",
            isLocallyStored = false,
            isCacheStored = false
        )
        val expected = SpotifySong(
            "songId",
            "songName",
            "artistName",
            "albumName",
            "releaseDate",
            "spotifyURL",
            "imageURL",
            isLocallyStored = false,
            isCacheStored = false
        )

        //every { spotifyCache.getSongByTerm("term") } returns null
        every { spotifyLocalStorage.getSongByTerm("term") } returns song

        val result = songRepository.getSongByTerm("term")

        assertEquals(result, expected)
        assertTrue(song.isLocallyStored)
    }

    @Test
    fun `when searchSongByTerm is called and song is in track service, should return song, and save song in local storage with term`() {
        val song = SpotifySong(
            "songId",
            "songName",
            "artistName",
            "albumName",
            "releaseDate",
            "spotifyURL",
            "imageURL",
            isLocallyStored = false,
            isCacheStored = false
        )
        val expected = SpotifySong(
            "songId",
            "songName",
            "artistName",
            "albumName",
            "releaseDate",
            "spotifyURL",
            "imageURL",
            isLocallyStored = false,
            isCacheStored = false
        )

        //every { spotifyCache.getSongByTerm("term") } returns null
        every { spotifyLocalStorage.getSongByTerm("term") } returns null
        every { spotifyTrackService.getSong("term") } returns song

        val result = songRepository.getSongByTerm("term")

        assertEquals(result, expected)
        verify { spotifyLocalStorage.insertSong("term",song) }
    }

    @Test
    fun `when searchSongByTerm is called and song is searched in wiki, should get song element with snippet`() {
        val song = SpotifySong(
            "",
            "snippet",
            "",
            "",
            "",
            "",
            "",
        )
        val expected = SpotifySong(
            "",
            "snippet",
            "",
            "",
            "",
            "",
            "",
        )
        //every { WikipediaAPI.getInfo("term") } returns song

        val result = songRepository.getSongByTerm("term")

        assertEquals(result, expected)
        //verify { WikipediaAPI.getInfo("term",song) }
    }

    @Test
    fun `when searchSongByTerm is called and song is not in wiki, should return EmptySong`() {
        //every { WikipediaAPI.getInfo("term") } returns EmptySong

        val result = songRepository.getSongByTerm("term")
        assertEquals(EmptySong, result)
    }

}