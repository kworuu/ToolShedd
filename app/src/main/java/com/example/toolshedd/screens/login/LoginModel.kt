package com.example.toolshedd.screens.login

import android.content.Context
import com.example.toolshedd.data.DatabaseHelper

class LoginModel(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun login(username: String, password: String): Boolean {
        return dbHelper.checkLogin(username, password)
    }
}
