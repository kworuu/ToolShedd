package com.example.toolshedd.screens.profile

class ProfileContract {
    interface View {
        fun displayUsername(username: String)
        fun navigateBack()
        fun logout()
    }

    interface Presenter {
        fun start(username: String?)
        fun onBackClicked()
        fun onLogoutClicked()
    }
}