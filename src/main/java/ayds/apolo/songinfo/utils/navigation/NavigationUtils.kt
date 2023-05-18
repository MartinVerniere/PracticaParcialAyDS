package ayds.apolo.songinfo.utils.navigation

import java.awt.Desktop
import java.net.URI

internal interface NavigationUtils {
  fun openExternalUrl(url: String)
}

internal class NavigationUtilsImpl : NavigationUtils {

  override fun openExternalUrl(url: String) {
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      Desktop.getDesktop().browse(URI(url))
    }
  }
}