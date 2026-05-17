package com.example.toolshedd.screens.addtool

import android.app.Activity
import android.net.Uri
import com.example.toolshedd.data.Tool

class AddToolPresenter(private val view: AddToolContract.View) : AddToolContract.Presenter {
    private val model = AddToolModel(view as Activity)

    override fun onAddToolClicked(
        name: String,
        brand: String,
        category: String,
        condition: String,
        owner: String,
        lat: Double,
        lng: Double,
        description: String,
        imageUri: Uri?
    ) {
        if (name.isBlank()) {
            view.showMessage("Tool name is required")
            return
        }

        view.showLoading(true)

        val tool = Tool(
            name          = name,
            brand         = brand,
            category      = category,
            condition     = condition,
            status        = "Available",
            ownerUsername = owner,
            lat           = lat,
            lng           = lng,
            description   = description
        )

        model.addTool(tool, imageUri,
            onSuccess = {
                view.showLoading(false)
                view.showMessage("Tool listed!")
                view.navigateBack()
            },
            onError = { error ->
                view.showLoading(false)
                view.showMessage(error)
            }
        )
    }
}