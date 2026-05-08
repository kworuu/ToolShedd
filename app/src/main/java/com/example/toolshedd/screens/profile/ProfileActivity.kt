package com.example.toolshedd.screens.profile

import android.app.Activity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import com.example.toolshedd.R
import com.example.toolshedd.data.DatabaseHelper
import com.example.toolshedd.data.Tool
import com.example.toolshedd.screens.login.LoginActivity
import com.example.toolshedd.utils.app
import com.example.toolshedd.utils.getButtonView
import com.example.toolshedd.utils.setTextViewText
import com.example.toolshedd.utils.start

class ProfileActivity : Activity(), ProfileContract.View {

    private lateinit var presenter: ProfileContract.Presenter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var toolAdapter: ToolAdapter
    private lateinit var toolList: ArrayList<Tool>
    private lateinit var listViewTools: ListView
    private var currentUsername: String = "User"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dbHelper = DatabaseHelper(this)
        presenter = ProfilePresenter(this)

        currentUsername = app().getUserInfo()?.username ?: "User"
        presenter.start(currentUsername)

        // --- ListView setup ---
        listViewTools = findViewById(R.id.listViewMyTools)
        toolList = dbHelper.getToolsByOwner(currentUsername)
        toolAdapter = ToolAdapter(this, toolList)
        listViewTools.adapter = toolAdapter

        // Click listener — show tool details (toast for now)
        listViewTools.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val tool = toolList[position]
            Toast.makeText(this, "${tool.name} — ${tool.status}", Toast.LENGTH_SHORT).show()
            // TODO: navigate to ToolDetailActivity and pass tool.id
        }

        // Long-click listener — delete a tool
        listViewTools.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            val tool = toolList[position]
            dbHelper.deleteTool(tool.id)
            toolList.removeAt(position)
            toolAdapter.notifyDataSetChanged()
            Toast.makeText(this, "${tool.name} removed", Toast.LENGTH_SHORT).show()
            true // consume the long click
        }

        // --- Other buttons ---
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener { presenter.onBackClicked() }

        getButtonView(R.id.btnLogout).setOnClickListener { presenter.onLogoutClicked() }
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