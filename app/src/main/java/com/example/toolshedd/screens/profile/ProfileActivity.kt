package com.example.toolshedd.screens.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.toolshedd.R
import com.example.toolshedd.data.DatabaseHelper
import com.example.toolshedd.data.Tool
import com.example.toolshedd.screens.home.HomeActivity
import com.example.toolshedd.screens.login.LoginActivity
import com.example.toolshedd.screens.map.MapActivity
import com.example.toolshedd.screens.tooldetail.ToolDetailActivity
import com.example.toolshedd.utils.app
import com.example.toolshedd.utils.getButtonView
import com.example.toolshedd.utils.setTextViewText
import com.example.toolshedd.utils.start

class ProfileActivity : Activity(), ProfileContract.View {

    private lateinit var presenter: ProfileContract.Presenter
    private lateinit var dbHelper: DatabaseHelper
    private var currentUsername: String = "User"

    // My Tools tab
    private lateinit var myToolsAdapter: ToolAdapter
    private lateinit var myToolsList: ArrayList<Tool>
    private lateinit var listViewMyTools: ListView

    // History tab
    private lateinit var historyAdapter: ToolAdapter
    private lateinit var historyList: ArrayList<Tool>
    private lateinit var listViewHistory: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dbHelper = DatabaseHelper(this)
        presenter = ProfilePresenter(this)
        currentUsername = app().getUserInfo()?.username ?: "User"
        presenter.start(currentUsername)

        // Dynamic avatar initials (Safe handling)
        val initials = if (currentUsername.contains("_")) {
            val parts = currentUsername.split("_")
            (parts[0].take(1) + parts[1].take(1)).uppercase()
        } else {
            currentUsername.take(2).uppercase()
        }
        findViewById<TextView>(R.id.tvAvatar)?.text = initials

        setupMyToolsList()
        setupHistoryList()
        setupTabs()
        setupBottomNavigation()

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { presenter.onBackClicked() }
        getButtonView(R.id.btnLogout).setOnClickListener { presenter.onLogoutClicked() }
    }

    override fun onResume() {
        super.onResume()
        refreshMyTools()
        refreshHistory()
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.navHome).setOnClickListener {
            start(HomeActivity::class.java)
            finish()
        }
        findViewById<View>(R.id.navMap).setOnClickListener {
            start(MapActivity::class.java)
            finish()
        }
        // Browse and Chat are placeholders for now
    }

    // ─────────────────────────────────────────
    // My Tools tab
    // ─────────────────────────────────────────

    private fun setupMyToolsList() {
        listViewMyTools = findViewById(R.id.listViewMyTools)
        myToolsList = dbHelper.getToolsByOwner(currentUsername)
        myToolsAdapter = ToolAdapter(this, myToolsList)
        listViewMyTools.adapter = myToolsAdapter

        listViewMyTools.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, ToolDetailActivity::class.java)
            intent.putExtra("TOOL_ID", myToolsList[position].id)
            startActivity(intent)
        }

        listViewMyTools.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            val tool = myToolsList[position]
            val deleted = dbHelper.deleteTool(tool.id)
            if (deleted > 0) {
                myToolsList.removeAt(position)
                myToolsAdapter.notifyDataSetChanged()
                Toast.makeText(this, "${tool.name} removed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Could not remove ${tool.name}", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun refreshMyTools() {
        myToolsList.clear()
        myToolsList.addAll(dbHelper.getToolsByOwner(currentUsername))
        myToolsAdapter.notifyDataSetChanged()
    }

    // ─────────────────────────────────────────
    // History tab
    // ─────────────────────────────────────────

    private fun setupHistoryList() {
        listViewHistory = findViewById(R.id.listViewHistory)
        historyList = dbHelper.getBorrowHistory(currentUsername)
        historyAdapter = ToolAdapter(this, historyList)
        listViewHistory.adapter = historyAdapter

        listViewHistory.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, ToolDetailActivity::class.java)
            intent.putExtra("TOOL_ID", historyList[position].id)
            startActivity(intent)
        }
    }

    private fun refreshHistory() {
        historyList.clear()
        historyList.addAll(dbHelper.getBorrowHistory(currentUsername))
        historyAdapter.notifyDataSetChanged()
        updateHistoryEmptyState()
    }

    private fun updateHistoryEmptyState() {
        val tvEmpty = findViewById<TextView>(R.id.tvHistoryEmpty)
        tvEmpty?.visibility = if (historyList.isEmpty()) View.VISIBLE else View.GONE
    }

    // ─────────────────────────────────────────
    // Tab switching
    // ─────────────────────────────────────────

    private enum class Tab { MY_TOOLS, HISTORY, REVIEWS }

    private fun setupTabs() {
        showTab(Tab.MY_TOOLS)
        findViewById<View>(R.id.tabMyTools).setOnClickListener { showTab(Tab.MY_TOOLS) }
        findViewById<View>(R.id.tabHistory).setOnClickListener  { showTab(Tab.HISTORY) }
        findViewById<View>(R.id.tabReviews).setOnClickListener  { showTab(Tab.REVIEWS) }
    }

    private fun showTab(tab: Tab) {
        listViewMyTools.visibility = if (tab == Tab.MY_TOOLS) View.VISIBLE else View.GONE
        listViewHistory.visibility  = if (tab == Tab.HISTORY)  View.VISIBLE else View.GONE

        val tvEmpty = findViewById<TextView>(R.id.tvHistoryEmpty)
        when (tab) {
            Tab.HISTORY -> {
                tvEmpty?.text = "No borrow history yet"
                updateHistoryEmptyState()
            }
            Tab.REVIEWS -> {
                tvEmpty?.text = "No reviews yet"
                tvEmpty?.visibility = View.VISIBLE
            }
            Tab.MY_TOOLS -> {
                tvEmpty?.visibility = View.GONE
            }
        }

        setTabActive(R.id.tabMyTools,  R.id.tabMyToolsIndicator,  tab == Tab.MY_TOOLS)
        setTabActive(R.id.tabHistory,  R.id.tabHistoryIndicator,  tab == Tab.HISTORY)
        setTabActive(R.id.tabReviews,  R.id.tabReviewsIndicator,  tab == Tab.REVIEWS)
    }

    private fun setTabActive(tabId: Int, indicatorId: Int, active: Boolean) {
        val primaryColor = resources.getColor(R.color.primary, theme)
        val subColor     = resources.getColor(R.color.text_sub, theme)
        val borderColor  = resources.getColor(R.color.border,   theme)

        val tabGroup = findViewById<ViewGroup>(tabId)
        val label    = tabGroup?.getChildAt(0) as? TextView
        label?.setTextColor(if (active) primaryColor else subColor)
        label?.typeface = if (active) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        findViewById<View>(indicatorId)
            ?.setBackgroundColor(if (active) primaryColor else borderColor)
    }

    // ─────────────────────────────────────────
    // View contract
    // ─────────────────────────────────────────

    override fun displayUsername(username: String) = setTextViewText(R.id.tvUsername, username)
    override fun navigateBack() = finish()
    override fun logout() { start(LoginActivity::class.java); finish() }
}