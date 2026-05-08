package com.example.toolshedd.screens.tooldetail

class ToolDetailContract {
    interface View {
        fun displayTool(name: String, brand: String, category: String, condition: String, owner: String, status: String)
        fun showMessage(message: String)
        fun navigateBack()
    }
    interface Presenter {
        fun start(toolId: Int)
        fun onBorrowClicked(toolId: Int, borrowerUsername: String)
        fun onBackClicked()
    }
}
