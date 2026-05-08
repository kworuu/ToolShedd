package com.example.toolshedd.screens.addtool

class AddToolContract {
    interface View {
        fun showMessage(message: String)
        fun navigateBack()
    }
    interface Presenter {
        fun onAddToolClicked(name: String, brand: String, category: String, condition: String, owner: String)
    }
}



