package com.example.toolshedd.screens.login

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.example.toolshedd.R
import com.example.toolshedd.screens.home.HomeActivity
import com.example.toolshedd.screens.register.RegisterActivity
import com.example.toolshedd.utils.getButtonView
import com.example.toolshedd.utils.getEditTextValue
import com.example.toolshedd.utils.start
import com.example.toolshedd.utils.toast

class LoginActivity : Activity(), LoginContract.View {

    private lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter = LoginPresenter(this, LoginModel(this))

        getButtonView(R.id.buttonLogin).setOnClickListener {
            val username = getEditTextValue(R.id.edittextUsername)
            val password = getEditTextValue(R.id.edittextPassword)

            presenter.login(username, password)
        }

        findViewById<TextView>(R.id.tvRegisterNow).setOnClickListener {
            presenter.onRegisterClicked()
        }
    }

    override fun showSuccessMessage() {
        toast("Login successful!")
    }

    override fun showInvalidCredentialMessage() {
        toast("Invalid credentials!")
    }

    override fun showEmptyMessage() {
        toast("Fields cannot be empty!")
    }

    override fun navigateToHomeScreen() {
        start(HomeActivity::class.java)
        finish()
    }

    override fun navigateToRegisterScreen() {
        start(RegisterActivity::class.java)
    }

    override fun showGenericErrorMessage() {
        toast("Unexpected error encountered.")
    }
}