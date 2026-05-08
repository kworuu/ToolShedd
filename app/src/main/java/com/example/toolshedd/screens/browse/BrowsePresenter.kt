package com.example.toolshedd.screens.browse

import android.app.Activity

class BrowsePresenter(private val view: BrowseContract.View) : BrowseContract.Presenter {
    private val model = BrowseModel(view as Activity)

    override fun start(currentUsername: String) {
        val tools = model.getAvailableTools(currentUsername)
        view.displayTools(tools)
    }

    override fun onToolClicked(toolId: Int) = view.navigateToDetail(toolId)
}