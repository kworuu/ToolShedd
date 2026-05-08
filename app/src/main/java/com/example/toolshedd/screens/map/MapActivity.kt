package com.example.toolshedd.screens.map

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.toolshedd.R
import com.example.toolshedd.screens.browse.BrowseActivity
import com.example.toolshedd.screens.profile.ProfileActivity
import com.example.toolshedd.screens.home.HomeActivity
import com.example.toolshedd.utils.start

class MapActivity : Activity(), MapContract.View {

    private lateinit var presenter: MapContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        presenter = MapPresenter(this)

        // Home nav
        findViewById<View>(R.id.navHome).setOnClickListener {
            presenter.onHomeTabClicked()
        }

        // Profile nav
        findViewById<View>(R.id.navProfile).setOnClickListener {
            presenter.onProfileTabClicked()
        }

        // FIX: Wire up Browse nav button (was in XML but never connected)
        // The Browse tab in activity_map.xml has no id — we use its position index
        // If you added an id (e.g. navBrowse) in the XML, swap to: R.id.navBrowse
        val bottomNav = findViewById<android.widget.LinearLayout>(R.id.bottomNav)
        // Child index 2 = Browse tab (Home=0, Map=1, Browse=2, Chat=3, Profile=4)
        bottomNav.getChildAt(2)?.setOnClickListener {
            start(BrowseActivity::class.java)
        }

        // Filter pill click listeners (visual feedback only for school project)
        setupFilterPills()

        // NOTE: The MapFragment in activity_map.xml uses Google Maps.
        // For the school project, a Google Maps API key is required in
        // AndroidManifest.xml under: <meta-data android:name="com.google.android.geo.API_KEY" .../>
        // Without it the map will show a grey tile with a "For development purposes only" watermark.
        // The layout and filter UI still work without the key.
    }

    private fun setupFilterPills() {
        val pills = listOf(R.id.filterAll, R.id.filterPower, R.id.filterHand, R.id.filterGarden)

        pills.forEach { pillId ->
            findViewById<TextView>(pillId)?.setOnClickListener { clicked ->
                // Reset all pills to inactive style
                pills.forEach { id ->
                    findViewById<TextView>(id)?.apply {
                        setBackgroundResource(R.drawable.bg_pill_inactive)
                        setTextColor(resources.getColor(R.color.text_sub, theme))
                        typeface = android.graphics.Typeface.DEFAULT
                    }
                }
                // Set the clicked pill to active style
                (clicked as TextView).apply {
                    setBackgroundResource(R.drawable.bg_pill_active)
                    setTextColor(resources.getColor(R.color.primary, theme))
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
            }
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