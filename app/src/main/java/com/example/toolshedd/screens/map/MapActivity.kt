package com.example.toolshedd.screens.map

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.example.toolshedd.R
import com.example.toolshedd.screens.profile.ProfileActivity
import com.example.toolshedd.screens.home.HomeActivity
import com.example.toolshedd.utils.start

class MapActivity : Activity(), MapContract.View {

    private lateinit var presenter: MapContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        presenter = MapPresenter(this)

        findViewById<View>(R.id.navHome).setOnClickListener {
            presenter.onHomeTabClicked()
        }

        findViewById<View>(R.id.navProfile).setOnClickListener {
            presenter.onProfileTabClicked()
        }
    }

    override fun navigateToHome() {
        start(HomeActivity::class.java)
        finish()
    }

    override fun navigateToProfile() {
        start(ProfileActivity::class.java)
        finish()
    }
}