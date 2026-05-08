package com.example.toolshedd.screens.addtool

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import com.example.toolshedd.R
import com.example.toolshedd.utils.app
import com.example.toolshedd.utils.getButtonView
import com.example.toolshedd.utils.getEditTextValue
import com.example.toolshedd.utils.toast

class AddToolActivity : Activity(), AddToolContract.View {

    private lateinit var presenter: AddToolPresenter
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerCondition: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tool)

        presenter = AddToolPresenter(this)

        // Spinners
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerCondition = findViewById(R.id.spinnerCondition)

        val categories = listOf("Power tools", "Hand tools", "Garden", "Measuring", "Other")
        val conditions = listOf("Like New", "Very Good", "Good", "Fair", "Poor")

        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCondition.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, conditions)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        getButtonView(R.id.btnAddTool).setOnClickListener {
            presenter.onAddToolClicked(
                name      = getEditTextValue(R.id.etToolName),
                brand     = getEditTextValue(R.id.etToolBrand),
                category  = spinnerCategory.selectedItem.toString(),
                condition = spinnerCondition.selectedItem.toString(),
                owner     = app().getUserInfo()?.username ?: ""
            )
        }
    }

    override fun showMessage(message: String) = toast(message)

    override fun navigateBack() { finish() }
}