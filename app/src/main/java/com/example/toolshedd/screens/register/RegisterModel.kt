package com.example.toolshedd.screens.register

class RegisterModel {
    fun validateRegistration(username: String, pass: String, confirmPass: String): String? {
        if (username.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            return "Please fill all fields"
        }
        if (pass != confirmPass) {
            return "Passwords do not match"
        }
        return null
    }
}