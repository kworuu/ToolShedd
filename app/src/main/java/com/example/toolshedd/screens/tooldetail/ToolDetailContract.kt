package com.example.toolshedd.screens.tooldetail

class ToolDetailContract {

    interface View {
        fun displayTool(
            name: String,
            brand: String,
            category: String,
            condition: String,
            owner: String,
            status: String
        )
        /** Switch the action button to "Return tool" mode. */
        fun showReturnButton()
        /** Switch the action button to "Request to borrow" mode. */
        fun showBorrowButton()
        /** Disable the action button (owned by viewer, already on loan to someone else, or unlisted). */
        fun disableActionButton(label: String)
        fun showMessage(message: String)
        fun navigateBack()
    }

    interface Presenter {
        fun start(toolId: Int)
        fun onBorrowClicked(toolId: Int, borrowerUsername: String)
        fun onReturnClicked(toolId: Int, borrowerUsername: String)
        fun onBackClicked()
    }
}