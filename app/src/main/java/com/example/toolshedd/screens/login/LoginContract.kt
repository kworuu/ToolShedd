package com.example.toolshedd.screens.login

class LoginContract {

    interface View {
        fun showSuccessMessage()
        fun showInvalidCredentialMessage()
        fun showEmptyMessage()
        fun navigateToHomeScreen()
        fun navigateToRegisterScreen()
        fun showGenericErrorMessage()
    }

    interface Presenter {
        fun login(username: String, password: String)
        fun onRegisterClicked()
    }
}
