package ayds.apolo.songinfo.home.controller

import ayds.apolo.songinfo.home.model.HomeModelInjector.homeModel
import ayds.apolo.songinfo.home.view.HomeViewInjector.homeView
import ayds.apolo.songinfo.utils.navigation.NavigationUtilsImpl

object HomeControllerInjector {
    fun init() {
      HomeControllerImpl(homeView, homeModel, NavigationUtilsImpl())
    }
}