package ayds.apolo.songinfo.home.controller

import ayds.apolo.songinfo.home.model.HomeModel
import ayds.apolo.songinfo.home.view.HomeUiEvent
import ayds.apolo.songinfo.home.view.HomeView
import ayds.apolo.songinfo.utils.navigation.NavigationUtils
import ayds.observer.Observer

interface HomeController

internal class HomeControllerImpl(
    private val homeView: HomeView,
    private val homeModel: HomeModel,
    private val navigationUtils: NavigationUtils,
) : HomeController {

    private val observer: Observer<HomeUiEvent> =
        Observer { value ->
            when (value) {
                HomeUiEvent.Search -> searchSong()
                is HomeUiEvent.OpenSongUrl-> openSongUrl()
            }
        }

    init {
        homeView.uiEventObservable.subscribe(observer)
    }

    private fun searchSong() {
        Thread {
            val result = homeModel.searchSong(homeView.uiState.searchTerm)
            homeView.updateSongInfo(result)
        }.start()
    }

    private fun openSongUrl() {
        navigationUtils.openExternalUrl(homeView.uiState.songUrl)
    }
}