package ayds.apolo.songinfo.home.view

data class HomeUiState(
  val songId: String = "",
  val windowTitle: String = "Song Info Apolo",
  val searchTerm: String = "",
  val songDescription: String = "",
  val songImageUrl: String = DEFAULT_IMAGE,
  val songUrl: String = "",
  val actionsEnabled : Boolean = false,
) {

  companion object {
    const val DEFAULT_IMAGE = "https://siqik.com/wp-content/uploads/2019/04/apollo.jpg"
  }
}