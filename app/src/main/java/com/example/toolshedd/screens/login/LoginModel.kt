package com.example.toolshedd.screens.login

class LoginModel {
    private val username = "test"
    private val password = "test"

    fun login(username: String, password: String): Boolean {

        return username.equals(this.username, false)
                && password.equals(this.password, false)
    }
}