package com.example.toolshedd.screens.home

class HomeModel {
    fun getWelcomeMessage(username: String): String {
        return "Welcome back, $username!"
    }
}