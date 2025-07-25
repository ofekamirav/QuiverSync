package org.example.quiversync.features.home


sealed class HomeState {
    data object Loading : HomeState()
    data class Loaded(
        val homePageData: HomePageData) : HomeState()
    data class Error(val message: String) : HomeState()
}