package com.example.toolshedd.screens.profile

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.toolshedd.R
import com.example.toolshedd.screens.login.LoginActivity
import com.example.toolshedd.utils.app
import com.example.toolshedd.utils.getButtonView
import com.example.toolshedd.utils.start
import com.example.toolshedd.utils.setTextViewText

class ProfileActivity : Activity(), ProfileContract.View {

    private lateinit var presenter: ProfileContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        presenter = ProfilePresenter(this)
        val username = app().getUserInfo()?.username ?: "User"

        presenter.start(username)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val btnLogout = getButtonView(R.id.btnLogout)

        ivBack.setOnClickListener {
            presenter.onBackClicked()
        }

        btnLogout.setOnClickListener {
            presenter.onLogoutClicked()
        }
    }

    override fun displayUsername(username: String) {
        setTextViewText(R.id.tvUsername, username)
    }

    override fun navigateBack() {
        finish()
    }

    override fun logout() {
        start(LoginActivity::class.java)
        finish()
    }
}