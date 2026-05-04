package com.example.toolshedd.screens.home

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.toolshedd.R
import com.example.toolshedd.screens.profile.ProfileActivity
import com.example.toolshedd.screens.map.MapActivity
import com.example.toolshedd.utils.app
import com.example.toolshedd.utils.start

class HomeActivity : Activity(), HomeContract.View {

    private lateinit var presenter: HomePresenter
    private var username: String = "User"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        presenter = HomePresenter(this, HomeModel())
        username = app().getUserInfo()?.username ?: "User"

        presenter.start(username)

        val navProfile = findViewById<View>(R.id.navProfile)
        navProfile.setOnClickListener {
            presenter.onProfileTabClicked(username)
        }

        val navMap = findViewById<View>(R.id.navMap)
        navMap.setOnClickListener {
            presenter.onMapTabClicked()
        }
    }

    override fun displayWelcomeMessage(username: String) {
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        tvWelcome.text = getString(R.string.welcome_back, username)
    }

    override fun navigateToProfile(username: String) {
        start(ProfileActivity::class.java)
    }

    override fun navigateToMap() {
        start(MapActivity::class.java)
    }
}