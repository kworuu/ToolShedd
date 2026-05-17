package com.example.toolshedd.screens.tooldetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.toolshedd.R
import com.example.toolshedd.utils.app
import com.example.toolshedd.utils.toast
import com.bumptech.glide.Glide
import com.example.toolshedd.data.ToolFirestoreHelper

class ToolDetailActivity : Activity(), ToolDetailContract.View {

    private lateinit var presenter: ToolDetailContract.Presenter
    private lateinit var btnAction: Button
    private var toolId: Int = -1
    private var currentUser: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tool_detail)

        toolId = intent.getIntExtra("TOOL_ID", -1)
        currentUser = app().getUserInfo()?.username ?: ""

        if (toolId == -1) {
            toast("Invalid tool")
            finish()
            return
        }

        btnAction = findViewById(R.id.btnBorrow)
        presenter = ToolDetailPresenter(this)

        // Pass the current username so the presenter can determine button state
        // We store it in the intent so the presenter can read it via (view as Activity).intent
        intent.putExtra("CURRENT_USER", currentUser)

        presenter.start(toolId)

        ToolFirestoreHelper.getToolMeta(toolId) { imageUrl, description ->
            runOnUiThread {
                if (imageUrl.isNotBlank()) {
                    val ivImage = findViewById<ImageView>(R.id.ivToolImage)
                    ivImage.visibility = View.VISIBLE
                    Glide.with(this).load(imageUrl).into(ivImage)
                }
                if (description.isNotBlank()) {
                    val layout = findViewById<View>(R.id.layoutDescription)
                    val tvDesc = findViewById<TextView>(R.id.tvDescription)
                    layout.visibility = View.VISIBLE
                    tvDesc.text = description
                }
            }
        }

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            presenter.onBackClicked()
        }
    }

    // ─────────────────────────────────────────
    // View contract implementations
    // ─────────────────────────────────────────

    override fun displayTool(
        name: String,
        brand: String,
        category: String,
        condition: String,
        owner: String,
        status: String
    ) {
        findViewById<TextView>(R.id.tvToolName).text = name
        findViewById<TextView>(R.id.tvBrand).text = brand
        findViewById<TextView>(R.id.tvCategory).text = category
        findViewById<TextView>(R.id.tvCondition).text = condition
        findViewById<TextView>(R.id.tvOwner).text = owner
        findViewById<TextView>(R.id.tvStatus).text = status
    }

    /** Shows a green "Return tool" button wired to the return flow. */
    override fun showReturnButton() {
        btnAction.visibility = View.VISIBLE
        btnAction.isEnabled = true
        btnAction.alpha = 1f
        btnAction.text = "Return tool"
        btnAction.backgroundTintList =
            android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#FF6B6B") // red-ish to signal return
            )
        btnAction.setOnClickListener {
            presenter.onReturnClicked(toolId, currentUser)
        }
    }

    /** Shows the default green "Request to borrow" button. */
    override fun showBorrowButton() {
        btnAction.visibility = View.VISIBLE
        btnAction.isEnabled = true
        btnAction.alpha = 1f
        btnAction.text = "Request to borrow"
        btnAction.backgroundTintList =
            android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#00B14F")
            )
        btnAction.setOnClickListener {
            presenter.onBorrowClicked(toolId, currentUser)
        }
    }

    /** Disables the button and shows a contextual label (e.g. "This is your tool"). */
    override fun disableActionButton(label: String) {
        btnAction.visibility = View.VISIBLE
        btnAction.isEnabled = false
        btnAction.alpha = 0.45f
        btnAction.text = label
        btnAction.setOnClickListener(null)
    }

    override fun showMessage(message: String) = toast(message)
    override fun navigateBack() = finish()

    // ─────────────────────────────────────────
    // Static helper so other screens can launch this activity cleanly
    // ─────────────────────────────────────────
    companion object {
        fun newIntent(from: Activity, toolId: Int): Intent =
            Intent(from, ToolDetailActivity::class.java).apply {
                putExtra("TOOL_ID", toolId)
            }
    }
}