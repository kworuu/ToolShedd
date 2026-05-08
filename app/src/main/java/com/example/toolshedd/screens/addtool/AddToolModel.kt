package com.example.toolshedd.screens.addtool

import android.content.Context
import com.example.toolshedd.data.DatabaseHelper
import com.example.toolshedd.data.Tool

class AddToolModel(private val context: Context) {
    private val db = DatabaseHelper(context)

    fun addTool(tool: Tool): Boolean {
        return db.addTool(tool) != -1L
    }
}

