package com.example.toolshedd.screens.map

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.toolshedd.R
import com.example.toolshedd.data.DatabaseHelper
import com.example.toolshedd.data.Tool
import com.example.toolshedd.screens.browse.BrowseActivity
import com.example.toolshedd.screens.home.HomeActivity
import com.example.toolshedd.screens.profile.ProfileActivity
import com.example.toolshedd.screens.tooldetail.ToolDetailActivity
import com.example.toolshedd.utils.app
import com.example.toolshedd.utils.start
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : Activity(), MapContract.View, OnMapReadyCallback {

    private lateinit var presenter: MapContract.Presenter
    private lateinit var dbHelper: DatabaseHelper
    private var googleMap: GoogleMap? = null

    // Holds all available tools and their markers so we can filter them
    private var allTools: List<Tool> = emptyList()
    private val markerToolMap = mutableMapOf<Marker, Tool>()
    private var activeFilter = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        dbHelper = DatabaseHelper(this)
        presenter = MapPresenter(this)

        // Bottom nav
        findViewById<View>(R.id.navHome).setOnClickListener { presenter.onHomeTabClicked() }
        findViewById<View>(R.id.navProfile).setOnClickListener { presenter.onProfileTabClicked() }
        findViewById<View>(R.id.navBrowse).setOnClickListener { start(BrowseActivity::class.java) }

        setupFilterPills()

        // Kick off async map load
        val mapFragment = fragmentManager.findFragmentById(R.id.mapFragment) as MapFragment
        mapFragment.getMapAsync(this)
    }

    // ─────────────────────────────────────────
    // OnMapReadyCallback
    // ─────────────────────────────────────────

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val currentUser = app().getUserInfo()?.username ?: ""
        allTools = dbHelper.getAvailableTools(currentUser)

        // Center camera on Cebu City (or wherever your mock data is)
        val center = LatLng(10.3157, 123.8854)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 14f))

        placeMarkers(allTools)

        // Show preview card when a pin is tapped
        map.setOnMarkerClickListener { marker ->
            val tool = markerToolMap[marker]
            if (tool != null) showPreviewCard(tool)
            true // consume event so default info window doesn't appear
        }

        // Hide preview card when tapping the map background
        map.setOnMapClickListener {
            hidePreviewCard()
        }
    }

    // ─────────────────────────────────────────
    // Markers
    // ─────────────────────────────────────────

    private fun placeMarkers(tools: List<Tool>) {
        googleMap?.let { map ->
            map.clear()
            markerToolMap.clear()

            tools.forEach { tool ->
                // Skip tools with no real location set
                if (tool.lat == 0.0 && tool.lng == 0.0) return@forEach

                val position = LatLng(tool.lat, tool.lng)
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(tool.name)
                        .snippet("@${tool.ownerUsername} · ${tool.condition}")
                        .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN))
                )
                if (marker != null) markerToolMap[marker] = tool
            }
        }
    }

    // ─────────────────────────────────────────
    // Bottom preview card
    // ─────────────────────────────────────────

    private fun showPreviewCard(tool: Tool) {
        val card = findViewById<View>(R.id.layoutBottomPreview)
        findViewById<TextView>(R.id.tvPreviewToolName).text = tool.name
        findViewById<TextView>(R.id.tvPreviewMeta).text =
            "Nearby · @${tool.ownerUsername} · ${tool.condition}"
        findViewById<Button>(R.id.btnPreviewBorrow).setOnClickListener {
            val intent = android.content.Intent(this, ToolDetailActivity::class.java)
            intent.putExtra("TOOL_ID", tool.id)
            startActivity(intent)
        }
        card.visibility = View.VISIBLE
    }

    private fun hidePreviewCard() {
        findViewById<View>(R.id.layoutBottomPreview).visibility = View.GONE
    }

    // ─────────────────────────────────────────
    // Filter pills — actually filter the markers now
    // ─────────────────────────────────────────

    private fun setupFilterPills() {
        val pills = listOf(
            R.id.filterAll   to "All",
            R.id.filterPower to "Power tools",
            R.id.filterHand  to "Hand tools",
            R.id.filterGarden to "Garden"
        )

        pills.forEach { (pillId, category) ->
            findViewById<TextView>(pillId)?.setOnClickListener {
                activeFilter = category
                updatePillStyles(pillId, pills.map { it.first })
                applyFilter()
                hidePreviewCard()
            }
        }
    }

    private fun applyFilter() {
        val filtered = if (activeFilter == "All") {
            allTools
        } else {
            allTools.filter { it.category == activeFilter }
        }
        placeMarkers(filtered)
    }

    private fun updatePillStyles(activePillId: Int, allPillIds: List<Int>) {
        allPillIds.forEach { id ->
            findViewById<TextView>(id)?.apply {
                setBackgroundResource(R.drawable.bg_pill_inactive)
                setTextColor(resources.getColor(R.color.text_sub, theme))
                typeface = android.graphics.Typeface.DEFAULT
            }
        }
        findViewById<TextView>(activePillId)?.apply {
            setBackgroundResource(R.drawable.bg_pill_active)
            setTextColor(resources.getColor(R.color.primary, theme))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
    }

    // ─────────────────────────────────────────
    // View contract
    // ─────────────────────────────────────────

    override fun navigateToHome() {
        start(HomeActivity::class.java)
        finish()
    }

    override fun navigateToProfile() {
        start(ProfileActivity::class.java)
        finish()
    }
}