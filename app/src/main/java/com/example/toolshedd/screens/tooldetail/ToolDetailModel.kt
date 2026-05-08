package com.example.toolshedd.screens.tooldetail

import android.content.ContentValues
import android.content.Context
import com.example.toolshedd.data.DatabaseHelper
import com.example.toolshedd.data.Tool

class ToolDetailModel(private val context: Context) {

    private val db = DatabaseHelper(context)

    fun getToolById(toolId: Int): Tool? = db.getToolById(toolId)

    fun isActiveBorrower(toolId: Int, username: String): Boolean =
        db.isActiveBorrower(toolId, username)

    fun requestBorrow(toolId: Int, borrowerUsername: String): Boolean {
        val writeDb = db.writableDatabase
        val cv = ContentValues().apply {
            put(DatabaseHelper.COL_BORROW_TOOL_ID, toolId)
            put(DatabaseHelper.COL_BORROW_BORROWER, borrowerUsername)
            put(DatabaseHelper.COL_BORROW_STATUS, "Active")
            put(
                DatabaseHelper.COL_BORROW_DATE,
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date())
            )
        }
        val result = writeDb.insert(DatabaseHelper.TABLE_BORROWS, null, cv)
        if (result != -1L) {
            db.updateToolStatus(toolId, "On Loan")
        }
        return result != -1L
    }

    /** Marks the borrow record as Returned and flips the tool back to Available. */
    fun returnTool(toolId: Int, borrowerUsername: String): Boolean =
        db.returnTool(toolId, borrowerUsername)
}