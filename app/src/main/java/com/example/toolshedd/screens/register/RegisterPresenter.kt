package com.example.toolshedd.screens.register

class RegisterPresenter(private val view: RegisterContract.View) : RegisterContract.Presenter {
    private val model = RegisterModel()

    override fun onSubmitClicked(username: String, password: String, confirmPass: String) {
        val error = model.validateRegistration(username, password, confirmPass)
        if (error != null) {
            view.showMessage(error)
        } else {
            view.showMessage("Registration Successful!")
            view.navigateBackToLogin()
        }
    }

    override fun onLoginClicked() {
        view.navigateBackToLogin()
    }
}