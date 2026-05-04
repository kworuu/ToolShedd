package com.example.toolshedd.screens.home

class HomeContract {
    interface View {
        fun displayWelcomeMessage(username: String)
        fun navigateToProfile(username: String)
        fun navigateToMap()
    }

    interface Presenter {
        fun start(username: String?)
        fun onProfileTabClicked(username: String)
        fun onMapTabClicked()
    }
}