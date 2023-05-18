package ayds.apolo.songinfo.home.model.repository.local.spotify.sqldb

import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.model.repository.local.spotify.SpotifyLocalStorage
import ayds.apolo.songinfo.utils.db.sql.SqlDB
import java.sql.SQLException

internal class SpotifySqlDBImpl(
  private val sqlQueries: SpotifySqlQueries,
  private val resultSetToSpotifySongMapper: ResultSetToSpotifySongMapper,
) : SqlDB(), SpotifyLocalStorage {

  override val dbUrl = sqlQueries.getDbUrl()

  init {
    initDatabase()
  }

  private fun initDatabase() {
    openConnection()
    createTablesIfNeeded()
    closeConnection()
  }

  private fun createTablesIfNeeded() {
    if (!tableCreated(sqlQueries.getSongsTableName())) createTables()
  }

  private fun createTables() {
    try {
      statement?.executeUpdate(sqlQueries.getCreateSongsTableQuery())
    } catch (e: SQLException) {
      println(e.message)
    }
  }

  override fun insertSong(query: String, song: SpotifySong) {
    openConnection()
    insertSongWithTerm(query, song)
    closeConnection()
  }

  private fun insertSongWithTerm(term: String, song: SpotifySong) {
    try {
      statement?.executeUpdate(sqlQueries.getInsertSongQuery(term, song))
    } catch (e: SQLException) {
      println("Error saving " + e.message)
    }
  }

  override fun updateSongTerm(query: String, songId: String) {
    openConnection()
    updateSongWithTerm(query, songId)
    closeConnection()
  }

  private fun updateSongWithTerm(term: String, songId: String) {
    try {
      statement?.executeUpdate(sqlQueries.getUpdateSongTermQuery(term, songId))
    } catch (e: SQLException) {
      println("Error saving " + e.message)
    }
  }

  override fun getSongByTerm(term: String): SpotifySong? {
    openConnection()
    val song = selectSongByTerm(term)
    closeConnection()
    return song
  }

  override fun getSongById(id: String): SpotifySong? {
    openConnection()
    val song = selectSongById(id)
    closeConnection()
    return song
  }

  private fun selectSongByTerm(term: String): SpotifySong? {
    var song: SpotifySong? = null
    try {
      val songResultSet = statement?.executeQuery(sqlQueries.getSelectSongsByTermQuery(term))
      song = songResultSet?.let { resultSetToSpotifySongMapper.map(it) }
    } catch (e: SQLException) {
      System.err.println("Get song by term error " + e.message)
    }
    return song
  }

  private fun selectSongById(id: String): SpotifySong? {
    var song: SpotifySong? = null
    try {
      val songResultSet = statement?.executeQuery(sqlQueries.getSelectSongsByIdQuery(id))
      song = songResultSet?.let { resultSetToSpotifySongMapper.map(it) }
    } catch (e: SQLException) {
      System.err.println("Get song by term error " + e.message)
    }
    return song
  }
}