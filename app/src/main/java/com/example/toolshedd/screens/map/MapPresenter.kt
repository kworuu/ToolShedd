package com.example.toolshedd.screens.map

class MapPresenter(private val view: MapContract.View) : MapContract.Presenter {

    override fun onHomeTabClicked() {
        view.navigateToHome()
    }

    override fun onProfileTabClicked() {
        view.navigateToProfile()
    }
}