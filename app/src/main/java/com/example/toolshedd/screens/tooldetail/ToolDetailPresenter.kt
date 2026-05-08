package com.example.toolshedd.screens.tooldetail

import android.app.Activity

class ToolDetailPresenter(private val view: ToolDetailContract.View) :
    ToolDetailContract.Presenter {

    private val model = ToolDetailModel(view as Activity)

    override fun start(toolId: Int) {
        val tool = model.getToolById(toolId) ?: run {
            view.showMessage("Tool not found")
            view.navigateBack()
            return
        }

        view.displayTool(
            tool.name, tool.brand, tool.category,
            tool.condition, tool.ownerUsername, tool.status
        )

        // Resolve which action button state to show
        val currentUser = (view as Activity).intent
            .getStringExtra("CURRENT_USER") ?: ""

        when {
            // Viewer owns this tool — no borrow action makes sense
            tool.ownerUsername == currentUser -> {
                view.disableActionButton("This is your tool")
            }
            // Viewer is the active borrower — let them return it
            tool.status == "On Loan" && model.isActiveBorrower(toolId, currentUser) -> {
                view.showReturnButton()
            }
            // Tool is on loan to someone else, or unlisted
            tool.status == "On Loan" -> {
                view.disableActionButton("Already on loan")
            }
            tool.status == "Unlisted" -> {
                view.disableActionButton("Not available")
            }
            // Tool is available — offer borrow
            else -> {
                view.showBorrowButton()
            }
        }
    }

    override fun onBorrowClicked(toolId: Int, borrowerUsername: String) {
        val success = model.requestBorrow(toolId, borrowerUsername)
        if (success) {
            view.showMessage("Borrow request sent!")
            view.showReturnButton()   // immediately flip to Return mode
        } else {
            view.showMessage("Could not send request. Try again.")
        }
    }

    override fun onReturnClicked(toolId: Int, borrowerUsername: String) {
        val success = model.returnTool(toolId, borrowerUsername)
        if (success) {
            view.showMessage("Tool returned successfully!")
            view.navigateBack()   // go back so Home refreshes the active borrows list
        } else {
            view.showMessage("Could not return tool. Try again.")
        }
    }

    override fun onBackClicked() = view.navigateBack()
}