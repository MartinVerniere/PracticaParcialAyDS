package ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb

import ayds.apolo.songinfo.home.model.entities.SpotifySong
import java.sql.ResultSet
import java.sql.SQLException

interface ResultSetToSpotifySongMapper {

  fun map(resultSet: ResultSet): SpotifySong?
}

internal class ResultSetToSpotifySongMapperImpl : ResultSetToSpotifySongMapper {

  override fun map(resultSet: ResultSet): SpotifySong? =
    try {
      if (resultSet.next()) {
        SpotifySong(
          id = resultSet.getString(ID_COLUMN),
          songName = resultSet.getString(NAME_COLUMN),
          artistName = resultSet.getString(ARTIST_COLUMN),
          albumName = resultSet.getString(ALBUM_COLUMN),
          releaseDate = resultSet.getString(RELEASE_DATE_COLUMN),
          spotifyUrl = resultSet.getString(SPOTIFY_URL_COLUMN),
          imageUrl = resultSet.getString(IMAGE_URL_COLUMN),
        )
      } else {
        null
      }
    } catch (e: SQLException) {
      e.printStackTrace()
      null
    }
}