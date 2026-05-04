package com.example.toolshedd.screens.home

class HomePresenter(val view: HomeContract.View, val model: HomeModel) : HomeContract.Presenter {

    override fun start(username: String?) {
        view.displayWelcomeMessage(username ?: "User")
    }

    override fun onProfileTabClicked(username: String) {
        view.navigateToProfile(username)
    }

    override fun onMapTabClicked() {
        view.navigateToMap()
    }
}