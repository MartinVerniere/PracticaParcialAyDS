package ayds.apolo.songinfo.home.view

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

interface HomeUiWindowComponents {
  val termTextField: JTextField
  val searchButton: JButton
  val descriptionPane: JTextPane
  val openSongButton: JButton
  val posterImageLabel: JLabel

  fun openWindow(title: String)
  fun enableActions(enable: Boolean)
}

private const val SEARCH = "Search"
private const val OPEN_SONG = "Open Song"

internal class HomeUiWindowComponentsImpl : HomeUiWindowComponents {

  override lateinit var termTextField: JTextField
  override lateinit var searchButton: JButton
  override lateinit var descriptionPane: JTextPane
  override lateinit var openSongButton: JButton
  override lateinit var posterImageLabel: JLabel

  private lateinit var contentPanel: JPanel

  init {
    buildUi()
    setStyle()
  }

  override fun openWindow(title: String) {
    val frame = JFrame(title)
    frame.contentPane = contentPanel
    frame.minimumSize = Dimension(600, 800)
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.pack()
    frame.isVisible = true
    frame.iconImage = ImageIcon(javaClass.getResource("/icon.png")).image
  }

  override fun enableActions(enable: Boolean) {
    openSongButton.isEnabled = enable
  }

  private fun buildUi() {
    addContentPanel()
    addSearchTermPanel()
    addSearchButton()
    addDescriptionPanel()
    addOpenSongButton()
    addPoster()
  }

  private fun addContentPanel() {
    contentPanel = JPanel()
    contentPanel.layout = BoxLayout(contentPanel, BoxLayout.PAGE_AXIS)
  }

  private fun addSearchTermPanel() {
    val searchTermPanel = JPanel()
    searchTermPanel.layout = BorderLayout()
    searchTermPanel.maximumSize = Dimension(400, 12)
    termTextField = JFormattedTextField()
    searchTermPanel.add(termTextField)
    contentPanel.add(searchTermPanel)
  }

  private fun addSearchButton() {
    val searchButtonPanel = JPanel()
    searchButtonPanel.layout = BorderLayout()
    searchButtonPanel.maximumSize = Dimension(400, 12)
    searchButton = JButton(SEARCH)
    searchButtonPanel.add(searchButton)
    contentPanel.add(searchButtonPanel)
  }

  private fun addDescriptionPanel() {
    val descriptionPanel = JPanel()
    descriptionPane = JTextPane()
    descriptionPane.isEditable = false
    descriptionPane.contentType = "text/html"
    descriptionPane.maximumSize = Dimension(600, 400)
    descriptionPanel.add(descriptionPane)
    contentPanel.add(descriptionPanel)
  }

  private fun addOpenSongButton() {
    val openSongButtonPanel = JPanel()
    openSongButtonPanel.layout = BorderLayout()
    openSongButtonPanel.maximumSize = Dimension(200, 12)
    openSongButton = JButton(OPEN_SONG)
    openSongButton.isEnabled = false
    openSongButtonPanel.add(openSongButton)
    contentPanel.add(openSongButtonPanel)
  }

  private fun addPoster() {
    val posterPanel = JPanel()
    posterImageLabel = JLabel()
    posterPanel.add(posterImageLabel)
    contentPanel.add(posterPanel)
  }

  private fun setStyle() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (ignored: ClassNotFoundException) {
    } catch (ignored: InstantiationException) {
    } catch (ignored: IllegalAccessException) {
    } catch (ignored: UnsupportedLookAndFeelException) {
    }
  }
}