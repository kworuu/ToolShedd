package com.example.toolshedd.screens.addtool

import android.content.Context
import android.net.Uri
import com.example.toolshedd.data.DatabaseHelper
import com.example.toolshedd.data.Tool
import com.example.toolshedd.data.ToolFirestoreHelper

class AddToolModel(private val context: Context) {
    private val db = DatabaseHelper(context)

    fun addTool(
        tool: Tool,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // 1. Save to SQLite first to get the ID
        val newId = db.addTool(tool)
        if (newId == -1L) {
            onError("Failed to save tool locally")
            return
        }

        val sqliteId = newId.toInt()

        // 2. If there's an image, upload it; otherwise just save description
        if (imageUri != null) {
            ToolFirestoreHelper.uploadToolWithImage(
                sqliteToolId  = sqliteId,
                ownerUsername = tool.ownerUsername,
                description   = tool.description,
                imageUri      = imageUri,
                onSuccess     = { onSuccess() },
                onError       = { error ->
                    // Tool is saved locally even if image upload fails
                    onSuccess()
                }
            )
        } else {
            if (tool.description.isNotBlank()) {
                ToolFirestoreHelper.saveToolMetadata(
                    sqliteToolId  = sqliteId,
                    ownerUsername = tool.ownerUsername,
                    description   = tool.description,
                    onSuccess     = { onSuccess() },
                    onError       = { onSuccess() } // still succeed locally
                )
            } else {
                onSuccess()
            }
        }
    }
}