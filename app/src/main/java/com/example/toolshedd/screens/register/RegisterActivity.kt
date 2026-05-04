package com.example.toolshedd.screens.register

import android.app.Activity
import android.os.Bundle
import com.example.toolshedd.R
import com.example.toolshedd.utils.getButtonView
import com.example.toolshedd.utils.getEditTextValue
import com.example.toolshedd.utils.toast

class RegisterActivity : Activity(), RegisterContract.View {

    private lateinit var presenter: RegisterContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        presenter = RegisterPresenter(this)

        getButtonView(R.id.btnSubmit).setOnClickListener {
            presenter.onSubmitClicked(
                getEditTextValue(R.id.etUsername),
                getEditTextValue(R.id.etPassword),
                getEditTextValue(R.id.etConfirmPassword)
            )
        }

        findViewById<android.view.View>(R.id.tvLoginNow).setOnClickListener {
            presenter.onLoginClicked()
        }
    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun navigateBackToLogin() {
        finish()
    }
}