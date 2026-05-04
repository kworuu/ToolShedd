package com.example.toolshedd.screens.register

class RegisterContract {
    interface View {
        fun showMessage(message: String)
        fun navigateBackToLogin()
    }

    interface Presenter {
        fun onSubmitClicked(username: String, password: String, confirmPass: String)
        fun onLoginClicked()
    }
}