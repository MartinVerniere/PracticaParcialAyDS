package ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb

import ayds.apolo.songinfo.home.model.entities.SpotifySong
internal interface SpotifySqlQueries {

  fun getDbUrl(): String
  fun getSongsTableName(): String
  fun getCreateSongsTableQuery(): String
  fun getInsertSongQuery(term: String, song: SpotifySong): String
  fun getUpdateSongTermQuery(term: String, songId: String): String
  fun getSelectSongsByTermQuery(term: String): String
  fun getSelectSongsByIdQuery(id: String): String

}

internal class SpotifySqlQueriesImpl : SpotifySqlQueries {

  override fun getDbUrl(): String = DB_URL

  override fun getSongsTableName(): String = SONGS_TABLE

  override fun getCreateSongsTableQuery(): String =
    "create table $SONGS_TABLE (" +
        "$ID_COLUMN string PRIMARY KEY, " +
        "$TERM_COLUMN string, " +
        "$NAME_COLUMN string, " +
        "$ARTIST_COLUMN integer, " +
        "$ALBUM_COLUMN string, " +
        "$RELEASE_DATE_COLUMN string, " +
        "$SPOTIFY_URL_COLUMN string, " +
        "$IMAGE_URL_COLUMN string)"

  override fun getInsertSongQuery(term: String, song: SpotifySong): String =
    "insert into $SONGS_TABLE values(" +
        "'${song.id}', " +
        "'$term', " +
        "'${getEscapedString(song.songName)}', " +
        "'${getEscapedString(song.artistName)}', " +
        "'${getEscapedString(song.albumName)}', " +
        "'${getEscapedString(song.releaseDate)}', " +
        "'${song.spotifyUrl}'," +
        "'${song.imageUrl}')"

  override fun getUpdateSongTermQuery(term: String, songId: String): String =
    "update $SONGS_TABLE set " +
        "$TERM_COLUMN = '$term' " +
        "where $ID_COLUMN = '$songId'"

  override fun getSelectSongsByTermQuery(term: String) =
    "select * from $SONGS_TABLE WHERE $TERM_COLUMN = '$term'"

  override fun getSelectSongsByIdQuery(id: String) =
    "select * from $SONGS_TABLE WHERE $ID_COLUMN = '$id'"

  private fun getEscapedString(string: String): String =
    string.replace("'".toRegex(), "''")
}