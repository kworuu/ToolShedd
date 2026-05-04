package com.example.toolshedd.screens.login

import android.app.Activity
import com.example.toolshedd.data.UserInfo
import com.example.toolshedd.utils.app

class LoginPresenter(private val view: LoginContract.View,
                     private val loginModel: LoginModel)
    : LoginContract.Presenter {

    private var isLoggedIn = false
    private val app = (view as Activity).app()

    override fun login(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty()) {
            if (loginModel.login(username, password)) {
                isLoggedIn = true
                app.setUserInfo(UserInfo(username, password))
                view.showSuccessMessage()
                view.navigateToHomeScreen()
            } else {
                view.showInvalidCredentialMessage()
            }
        } else {
            view.showEmptyMessage()
        }
    }

    override fun onRegisterClicked() {
        view.navigateToRegisterScreen()
    }
}