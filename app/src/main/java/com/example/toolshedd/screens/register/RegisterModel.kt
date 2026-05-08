package com.example.toolshedd.screens.register

import android.content.Context
import com.example.toolshedd.data.DatabaseHelper

class RegisterModel(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    /**
     * Returns null on success, or an error message string.
     * FIX: Minimum password length raised from 6 to 8 to match the UI hint ("min 8 characters").
     */
    fun validateAndRegister(username: String, pass: String, confirmPass: String): String? {
        if (username.isEmpty() || pass.isEmpty() || confirmPass.isEmpty())
            return "Please fill all fields"
        if (pass != confirmPass)
            return "Passwords do not match"
        if (pass.length < 8)
            return "Password must be at least 8 characters"
        return dbHelper.registerUser(username, pass)
    }
}