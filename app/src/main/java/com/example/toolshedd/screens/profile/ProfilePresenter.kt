package com.example.toolshedd.screens.profile

class ProfilePresenter(private val view: ProfileContract.View) : ProfileContract.Presenter {
    private val model = ProfileModel()

    override fun start(username: String?) {
        view.displayUsername(username ?: "User")
    }

    override fun onBackClicked() {
        view.navigateBack()
    }

    override fun onLogoutClicked() {
        view.logout()
    }
}