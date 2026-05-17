package com.example.toolshedd.screens.addtool

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import com.example.toolshedd.R
import com.example.toolshedd.utils.app
import com.example.toolshedd.utils.getButtonView
import com.example.toolshedd.utils.getEditTextValue
import com.example.toolshedd.utils.toast

class AddToolActivity : Activity(), AddToolContract.View {

    private lateinit var presenter: AddToolPresenter
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerCondition: Spinner
    private lateinit var tvLocationLabel: TextView
    private lateinit var ivToolImagePreview: ImageView
    private lateinit var progressBar: ProgressBar

    private var pickedLat: Double = 0.0
    private var pickedLng: Double = 0.0
    private var pickedImageUri: Uri? = null

    companion object {
        private const val REQUEST_LOCATION = 2001
        private const val REQUEST_IMAGE    = 2002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tool)

        presenter          = AddToolPresenter(this)
        spinnerCategory    = findViewById(R.id.spinnerCategory)
        spinnerCondition   = findViewById(R.id.spinnerCondition)
        tvLocationLabel    = findViewById(R.id.tvLocationLabel)
        ivToolImagePreview = findViewById(R.id.ivToolImagePreview)
        progressBar        = findViewById(R.id.progressBar)

        val categories = listOf("Power tools", "Hand tools", "Garden", "Measuring", "Other")
        val conditions = listOf("Like New", "Very Good", "Good", "Fair", "Poor")

        spinnerCategory.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCondition.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, conditions)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Pick image from gallery
        findViewById<View>(R.id.btnPickImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(intent, REQUEST_IMAGE)
        }

        // Open location picker
        findViewById<View>(R.id.btnPickLocation).setOnClickListener {
            startActivityForResult(
                Intent(this, LocationPickerActivity::class.java),
                REQUEST_LOCATION
            )
        }

        getButtonView(R.id.btnAddTool).setOnClickListener {
            if (pickedLat == 0.0 && pickedLng == 0.0) {
                toast("Please set a location for your tool")
                return@setOnClickListener
            }
            presenter.onAddToolClicked(
                name        = getEditTextValue(R.id.etToolName),
                brand       = getEditTextValue(R.id.etToolBrand),
                category    = spinnerCategory.selectedItem.toString(),
                condition   = spinnerCondition.selectedItem.toString(),
                owner       = app().getUserInfo()?.username ?: "",
                lat         = pickedLat,
                lng         = pickedLng,
                description = getEditTextValue(R.id.etDescription),
                imageUri    = pickedImageUri
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == REQUEST_IMAGE && resultCode == RESULT_OK -> {
                pickedImageUri = data?.data
                ivToolImagePreview.setImageURI(pickedImageUri)
                ivToolImagePreview.visibility = View.VISIBLE
            }
            requestCode == REQUEST_LOCATION && resultCode == RESULT_OK && data != null -> {
                pickedLat = data.getDoubleExtra("LAT", 0.0)
                pickedLng = data.getDoubleExtra("LNG", 0.0)
                val label = data.getStringExtra("LABEL")
                    ?: "%.4f, %.4f".format(pickedLat, pickedLng)
                tvLocationLabel.text = "📍 $label"
            }
        }
    }

    override fun showMessage(message: String) = toast(message)
    override fun navigateBack() { finish() }
    override fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        getButtonView(R.id.btnAddTool).isEnabled = !show
    }
}