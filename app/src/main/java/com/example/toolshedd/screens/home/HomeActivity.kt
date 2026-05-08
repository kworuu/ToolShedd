package com.example.toolshedd.screens.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.example.toolshedd.R
import com.example.toolshedd.data.DatabaseHelper
import com.example.toolshedd.data.Tool
import com.example.toolshedd.screens.addtool.AddToolActivity
import com.example.toolshedd.screens.browse.BrowseActivity
import com.example.toolshedd.screens.profile.ProfileActivity
import com.example.toolshedd.screens.profile.ToolAdapter
import com.example.toolshedd.screens.map.MapActivity
import com.example.toolshedd.screens.tooldetail.ToolDetailActivity
import com.example.toolshedd.utils.app
import com.example.toolshedd.utils.start

class HomeActivity : Activity(), HomeContract.View {

    private lateinit var presenter: HomePresenter
    private lateinit var db: DatabaseHelper
    private var username: String = "User"

    // Active borrows list
    private lateinit var activeBorrowsAdapter: ToolAdapter
    private lateinit var activeBorrowsList: ArrayList<Tool>

    // Nearby tools list
    private lateinit var nearbyToolsAdapter: ToolAdapter
    private lateinit var nearbyToolsList: ArrayList<Tool>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        db = DatabaseHelper(this)
        presenter = HomePresenter(this, HomeModel())
        username = app().getUserInfo()?.username ?: "User"
        presenter.start(username)

        setupActiveBorrowsList()
        setupNearbyToolsList()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        // Refresh lists when returning from AddTool or ToolDetail
        refreshLists()
    }

    private fun setupActiveBorrowsList() {
        activeBorrowsList = db.getActiveBorrows(username)
        activeBorrowsAdapter = ToolAdapter(this, activeBorrowsList)
        val lvBorrows = findViewById<ListView>(R.id.lvActiveBorrows)
        lvBorrows.adapter = activeBorrowsAdapter
        lvBorrows.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            navigateToDetail(activeBorrowsList[position].id)
        }
        updateEmptyState(R.id.tvNoBorrows, activeBorrowsList)
    }

    private fun setupNearbyToolsList() {
        nearbyToolsList = db.getAvailableTools(username)
        nearbyToolsAdapter = ToolAdapter(this, nearbyToolsList)
        val lvNearby = findViewById<ListView>(R.id.lvNearbyTools)
        lvNearby.adapter = nearbyToolsAdapter
        lvNearby.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            navigateToDetail(nearbyToolsList[position].id)
        }
        updateEmptyState(R.id.tvNoNearby, nearbyToolsList)
    }

    private fun refreshLists() {
        activeBorrowsList.clear()
        activeBorrowsList.addAll(db.getActiveBorrows(username))
        activeBorrowsAdapter.notifyDataSetChanged()

        nearbyToolsList.clear()
        nearbyToolsList.addAll(db.getAvailableTools(username))
        nearbyToolsAdapter.notifyDataSetChanged()

        updateEmptyState(R.id.tvNoBorrows, activeBorrowsList)
        updateEmptyState(R.id.tvNoNearby, nearbyToolsList)
    }

    private fun updateEmptyState(textViewId: Int, list: ArrayList<Tool>) {
        val tv = findViewById<TextView>(textViewId)
        tv?.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun navigateToDetail(toolId: Int) {
        val intent = Intent(this, ToolDetailActivity::class.java)
        intent.putExtra("TOOL_ID", toolId)
        startActivity(intent)
    }

    private fun setupNavigation() {
        findViewById<View>(R.id.navMap).setOnClickListener { presenter.onMapTabClicked() }
        findViewById<View>(R.id.navProfile).setOnClickListener { presenter.onProfileTabClicked(username) }
        findViewById<View>(R.id.navBrowse).setOnClickListener { start(BrowseActivity::class.java) }
        findViewById<View>(R.id.fabAddTool).setOnClickListener { start(AddToolActivity::class.java) }
    }

    override fun displayWelcomeMessage(username: String) {
        findViewById<TextView>(R.id.tvWelcome).text = getString(R.string.welcome_back, username)
    }

    override fun navigateToProfile(username: String) { start(ProfileActivity::class.java) }
    override fun navigateToMap() { start(MapActivity::class.java) }
}