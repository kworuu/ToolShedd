package com.example.toolshedd.screens.map

class MapContract {
    interface View {
        fun navigateToHome()
        fun navigateToProfile()
    }

    interface Presenter {
        fun onHomeTabClicked()
        fun onProfileTabClicked()
    }
}