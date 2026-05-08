package com.example.toolshedd.screens.browse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.example.toolshedd.R
import com.example.toolshedd.data.Tool
import com.example.toolshedd.screens.home.HomeActivity
import com.example.toolshedd.screens.map.MapActivity
import com.example.toolshedd.screens.profile.ProfileActivity
import com.example.toolshedd.screens.profile.ToolAdapter
import com.example.toolshedd.screens.tooldetail.ToolDetailActivity
import com.example.toolshedd.utils.app
import com.example.toolshedd.utils.start

class BrowseActivity : Activity(), BrowseContract.View {

    private lateinit var presenter: BrowsePresenter
    private lateinit var adapter: ToolAdapter
    private lateinit var toolList: ArrayList<Tool>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)

        presenter = BrowsePresenter(this)
        toolList = ArrayList()
        adapter = ToolAdapter(this, toolList)

        val listView = findViewById<ListView>(R.id.listViewBrowse)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            presenter.onToolClicked(toolList[position].id)
        }

        // FIX: Wire up bottom nav buttons (were declared in XML but never connected)
        findViewById<android.view.View>(R.id.navHome).setOnClickListener {
            start(HomeActivity::class.java)
            finish()
        }
        findViewById<android.view.View>(R.id.navMap).setOnClickListener {
            start(MapActivity::class.java)
            finish()
        }
        findViewById<android.view.View>(R.id.navProfile).setOnClickListener {
            start(ProfileActivity::class.java)
        }

        val username = app().getUserInfo()?.username ?: ""
        presenter.start(username)
    }

    // Refresh list when returning from ToolDetail (a tool may now be On Loan)
    override fun onResume() {
        super.onResume()
        val username = app().getUserInfo()?.username ?: ""
        presenter.start(username)
    }

    override fun displayTools(tools: ArrayList<Tool>) {
        toolList.clear()
        toolList.addAll(tools)
        adapter.notifyDataSetChanged()

        val tvEmpty = findViewById<TextView>(R.id.tvBrowseEmpty)
        tvEmpty.visibility = if (tools.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    override fun navigateToDetail(toolId: Int) {
        val intent = Intent(this, ToolDetailActivity::class.java)
        intent.putExtra("TOOL_ID", toolId)
        startActivity(intent)
    }
}