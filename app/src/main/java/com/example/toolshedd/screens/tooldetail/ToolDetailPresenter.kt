package com.example.toolshedd.screens.tooldetail

import android.app.Activity

class ToolDetailPresenter(private val view: ToolDetailContract.View) : ToolDetailContract.Presenter {
    private val model = ToolDetailModel(view as Activity)

    override fun start(toolId: Int) {
        val tool = model.getToolById(toolId)
        if (tool != null) {
            view.displayTool(tool.name, tool.brand, tool.category, tool.condition, tool.ownerUsername, tool.status)
        } else {
            view.showMessage("Tool not found")
            view.navigateBack()
        }
    }

    override fun onBorrowClicked(toolId: Int, borrowerUsername: String) {
        val success = model.requestBorrow(toolId, borrowerUsername)
        view.showMessage(if (success) "Borrow request sent!" else "Could not send request")
    }

    override fun onBackClicked() = view.navigateBack()
}
