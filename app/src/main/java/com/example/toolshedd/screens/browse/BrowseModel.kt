package com.example.toolshedd.screens.browse

import android.content.Context
import com.example.toolshedd.data.DatabaseHelper
import com.example.toolshedd.data.Tool

class BrowseModel(private val context: Context) {
    private val db = DatabaseHelper(context)
    fun getAvailableTools(excludeUsername: String): ArrayList<Tool> {
        return db.getAvailableTools(excludeUsername)
    }
}