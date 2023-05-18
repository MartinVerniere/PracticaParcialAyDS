package ayds.apolo.songinfo.home.view

object HomeViewInjector {
    val homeView : HomeView = HomeViewImpl(
        HomeUiWindowComponentsImpl(),
        SongDescriptionHelperImpl()
    )
}