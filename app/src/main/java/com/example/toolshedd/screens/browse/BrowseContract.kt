package com.example.toolshedd.screens.browse

class BrowseContract {
    interface View {
        fun displayTools(tools: ArrayList<com.example.toolshedd.data.Tool>)
        fun navigateToDetail(toolId: Int)
    }
    interface Presenter {
        fun start(currentUsername: String)
        fun onToolClicked(toolId: Int)
    }
}