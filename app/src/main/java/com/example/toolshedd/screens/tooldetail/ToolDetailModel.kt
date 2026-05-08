package com.example.toolshedd.screens.tooldetail

import android.content.Context
import android.content.ContentValues
import com.example.toolshedd.data.DatabaseHelper
import com.example.toolshedd.data.Tool

class ToolDetailModel(private val context: Context) {
    private val db = DatabaseHelper(context)

    fun getToolById(toolId: Int): Tool? {
        val readDb = db.readableDatabase
        val cursor = readDb.query(
            DatabaseHelper.TABLE_TOOLS, null,
            "${DatabaseHelper.COL_TOOL_ID} = ?",
            arrayOf(toolId.toString()),
            null, null, null
        )
        var tool: Tool? = null
        if (cursor.moveToFirst()) {
            tool = Tool(
                id            = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TOOL_ID)),
                name          = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TOOL_NAME)),
                brand         = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TOOL_BRAND)),
                category      = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TOOL_CATEGORY)),
                condition     = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TOOL_CONDITION)),
                status        = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TOOL_STATUS)),
                ownerUsername = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TOOL_OWNER))
            )
        }
        cursor.close()
        return tool
    }

    fun requestBorrow(toolId: Int, borrowerUsername: String): Boolean {
        val writeDb = db.writableDatabase

        // Insert borrow request
        val cv = ContentValues().apply {
            put(DatabaseHelper.COL_BORROW_TOOL_ID, toolId)
            put(DatabaseHelper.COL_BORROW_BORROWER, borrowerUsername)
            put(DatabaseHelper.COL_BORROW_STATUS, "Active")
            put(DatabaseHelper.COL_BORROW_DATE, java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()))
        }
        val result = writeDb.insert(DatabaseHelper.TABLE_BORROWS, null, cv)

        // Update tool status to On Loan
        if (result != -1L) {
            val statusCv = ContentValues().apply { put(DatabaseHelper.COL_TOOL_STATUS, "On Loan") }
            writeDb.update(DatabaseHelper.TABLE_TOOLS, statusCv, "${DatabaseHelper.COL_TOOL_ID} = ?", arrayOf(toolId.toString()))
        }
        return result != -1L
    }
}

