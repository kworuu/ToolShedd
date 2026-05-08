package com.example.toolshedd.screens.browse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.example.toolshedd.R
import com.example.toolshedd.data.Tool
import com.example.toolshedd.screens.profile.ToolAdapter
import com.example.toolshedd.screens.tooldetail.ToolDetailActivity
import com.example.toolshedd.utils.app

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

        val username = app().getUserInfo()?.username ?: ""
        presenter.start(username)
    }

    override fun displayTools(tools: ArrayList<Tool>) {
        toolList.clear()
        toolList.addAll(tools)
        adapter.notifyDataSetChanged()

        // Show empty state message if no tools
        val tvEmpty = findViewById<TextView>(R.id.tvBrowseEmpty)
        tvEmpty.visibility = if (tools.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    override fun navigateToDetail(toolId: Int) {
        val intent = Intent(this, ToolDetailActivity::class.java)
        intent.putExtra("TOOL_ID", toolId)
        startActivity(intent)
    }
}