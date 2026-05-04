package com.example.toolshedd.screens.profile

class ProfileModel {
    fun getUserStats(): Map<String, String> {
        return mapOf(
            "Rating" to "4.9",
            "Lends" to "12",
            "Borrows" to "8",
            "Listed" to "5"
        )
    }
}