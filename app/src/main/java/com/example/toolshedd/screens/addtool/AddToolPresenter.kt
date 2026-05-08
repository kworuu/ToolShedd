// ─── AddToolPresenter.kt ─────────────────────────────────────────────────────
package com.example.toolshedd.screens.addtool

import android.app.Activity
import com.example.toolshedd.data.Tool

class AddToolPresenter(private val view: AddToolContract.View) : AddToolContract.Presenter {
    private val model = AddToolModel(view as Activity)

    override fun onAddToolClicked(name: String, brand: String, category: String, condition: String, owner: String) {
        if (name.isBlank()) {
            view.showMessage("Tool name is required")
            return
        }
        val tool = Tool(
            name     = name,
            brand    = brand,
            category = category,
            condition= condition,
            status   = "Available",
            ownerUsername = owner
        )
        val success = model.addTool(tool)
        if (success) {
            view.showMessage("Tool listed!")
            view.navigateBack()
        } else {
            view.showMessage("Failed to add tool. Try again.")
        }
    }
}