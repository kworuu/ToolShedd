package com.example.toolshedd.screens.addtool

class AddToolContract {
    interface View {
        fun showMessage(message: String)
        fun navigateBack()
        fun showLoading(show: Boolean)
    }
    interface Presenter {
        fun onAddToolClicked(name: String,
                             brand: String,
                             category: String,
                             condition: String,
                             owner: String,
                             lat: Double, lng: Double,
                             description: String,   // ← new
                             imageUri: android.net.Uri?
        )
    }
}



