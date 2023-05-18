package ayds.apolo.songinfo.home.view

import ayds.apolo.songinfo.home.model.entities.EmptySong
import ayds.apolo.songinfo.home.model.entities.SearchResult
import ayds.apolo.songinfo.home.model.entities.SpotifySong
import ayds.apolo.songinfo.home.view.HomeUiState.Companion.DEFAULT_IMAGE
import ayds.observer.Observable
import ayds.observer.Subject
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.SwingUtilities

interface HomeView {
  val uiEventObservable: Observable<HomeUiEvent>
  val uiState: HomeUiState

  fun openView()
  fun updateSongInfo(song: SearchResult)
}

internal class HomeViewImpl constructor(
  private val uiComponents: HomeUiWindowComponents,
  private val songDescriptionHelper: SongDescriptionHelper
) : HomeView {

  private val onActionSubject = Subject<HomeUiEvent>()

  override val uiEventObservable: Observable<HomeUiEvent> = onActionSubject
  override var uiState: HomeUiState = HomeUiState()

  init {
    initListeners()
    updateSongImage()
  }

  override fun openView() {
    uiComponents.openWindow(uiState.windowTitle)
  }

  private fun initListeners() {
    with(uiComponents) {
      searchButton.addActionListener { searchAction() }
      openSongButton.addActionListener { notifyOpenSongAction() }
    }
  }

  private fun searchAction() {
    updateSearchTermState()
    updateDisabledActionsState()
    updateMoreDetailsState()
    notifySearchAction()
  }

  private fun notifySearchAction() {
    onActionSubject.notify(HomeUiEvent.Search)
  }

  private fun notifyOpenSongAction() {
    onActionSubject.notify(HomeUiEvent.OpenSongUrl)
  }

  private fun updateSearchTermState() {
    uiState = uiState.copy(searchTerm = uiComponents.termTextField.text)
  }

  private fun updateDisabledActionsState() {
    uiState = uiState.copy(actionsEnabled = false)
  }

  override fun updateSongInfo(song: SearchResult) {
    SwingUtilities.invokeLater { updateSongInfoSync(song) }
  }

  private fun updateSongInfoSync(song: SearchResult) {
    updateUiState(song)
    updateSongDescription()
    updateSongImage()
    updateMoreDetailsState()
  }

  private fun updateUiState(song: SearchResult) {
    when(song) {
      is SpotifySong -> updateSongUiState(song)
      EmptySong -> updateNoResultsUiState()
    }
  }

  private fun updateSongUiState(song: SpotifySong) {
    uiState = uiState.copy(
      songId = song.id,
      songImageUrl = song.imageUrl,
      songUrl = song.spotifyUrl,
      songDescription = songDescriptionHelper.getSongDescriptionText(song),
      actionsEnabled = true
    )
  }

  private fun updateNoResultsUiState() {
    uiState = uiState.copy(
      songId = "",
      songImageUrl = DEFAULT_IMAGE,
      songUrl = "",
      songDescription = songDescriptionHelper.getSongDescriptionText(),
      actionsEnabled = false
    )
  }

  private fun updateSongDescription() {
    uiComponents.descriptionPane.text = uiState.songDescription
  }

  private fun updateSongImage() {
    getImageFromUrl(uiState.songImageUrl)?.let { setPosterImage(it) }
  }

  private fun getImageFromUrl(imageUrl: String): BufferedImage? =
    try {
      val url = URL(imageUrl)
      ImageIO.read(url)
    } catch (e: Exception) {
      null
    }

  private fun setPosterImage(image: BufferedImage) {
    uiComponents.posterImageLabel.icon = ImageIcon(image)
  }

  private fun updateMoreDetailsState() {
    uiComponents.enableActions(uiState.actionsEnabled)
  }
}