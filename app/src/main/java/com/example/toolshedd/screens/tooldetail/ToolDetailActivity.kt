package com.example.toolshedd.screens.tooldetail

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.toolshedd.R
import com.example.toolshedd.utils.app
import com.example.toolshedd.utils.getButtonView
import com.example.toolshedd.utils.toast

class ToolDetailActivity : Activity(), ToolDetailContract.View {

    private lateinit var presenter: ToolDetailContract.Presenter
    private var toolId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tool_detail)

        toolId = intent.getIntExtra("TOOL_ID", -1)
        presenter = ToolDetailPresenter(this)

        if (toolId == -1) {
            toast("Invalid tool")
            finish()
            return
        }

        presenter.start(toolId)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { presenter.onBackClicked() }

        getButtonView(R.id.btnBorrow).setOnClickListener {
            val username = app().getUserInfo()?.username ?: ""
            presenter.onBorrowClicked(toolId, username)
        }
    }

    override fun displayTool(name: String, brand: String, category: String, condition: String, owner: String, status: String) {
        findViewById<TextView>(R.id.tvToolName).text   = name
        findViewById<TextView>(R.id.tvBrand).text      = brand
        findViewById<TextView>(R.id.tvCategory).text   = category
        findViewById<TextView>(R.id.tvCondition).text  = condition
        findViewById<TextView>(R.id.tvOwner).text      = owner
        findViewById<TextView>(R.id.tvStatus).text     = status
        // tvDistance is optional — leave as "—" for now
    }

    override fun showMessage(message: String) = toast(message)
    override fun navigateBack() = finish()
}