package com.example.toolshedd.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "toolshedd.db"
        const val DATABASE_VERSION = 1

        // --- Users table ---
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"

        // --- Tools table ---
        const val TABLE_TOOLS = "tools"
        const val COL_TOOL_ID = "id"
        const val COL_TOOL_NAME = "name"
        const val COL_TOOL_BRAND = "brand"
        const val COL_TOOL_CATEGORY = "category"
        const val COL_TOOL_CONDITION = "condition"
        const val COL_TOOL_STATUS = "status"          // "Available", "On Loan", "Unlisted"
        const val COL_TOOL_OWNER = "owner_username"

        // --- Borrow Requests table ---
        const val TABLE_BORROWS = "borrow_requests"
        const val COL_BORROW_ID = "id"
        const val COL_BORROW_TOOL_ID = "tool_id"
        const val COL_BORROW_BORROWER = "borrower_username"
        const val COL_BORROW_STATUS = "status"        // "Active", "Returned", "Pending"
        const val COL_BORROW_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT UNIQUE NOT NULL,
                $COL_PASSWORD TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_TOOLS (
                $COL_TOOL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TOOL_NAME TEXT NOT NULL,
                $COL_TOOL_BRAND TEXT,
                $COL_TOOL_CATEGORY TEXT,
                $COL_TOOL_CONDITION TEXT,
                $COL_TOOL_STATUS TEXT DEFAULT 'Available',
                $COL_TOOL_OWNER TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_BORROWS (
                $COL_BORROW_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_BORROW_TOOL_ID INTEGER NOT NULL,
                $COL_BORROW_BORROWER TEXT NOT NULL,
                $COL_BORROW_STATUS TEXT DEFAULT 'Pending',
                $COL_BORROW_DATE TEXT,
                FOREIGN KEY($COL_BORROW_TOOL_ID) REFERENCES $TABLE_TOOLS($COL_TOOL_ID)
            )
        """.trimIndent())

        // Seed a default user so login still works during development
        val user = ContentValues().apply {
            put(COL_USERNAME, "test")
            put(COL_PASSWORD, "test")
        }
        db.insert(TABLE_USERS, null, user)

        // Seed some sample tools for that user
        val sampleTools = listOf(
            arrayOf("Hand saw", "Stanley · FatMax", "Hand tools", "Good", "Available"),
            arrayOf("Power washer", "Kärcher · K5", "Power tools", "Very Good", "On Loan"),
            arrayOf("Pipe wrench", "Ridgid · 14 inch", "Hand tools", "Fair", "Unlisted")
        )
        for (t in sampleTools) {
            val cv = ContentValues().apply {
                put(COL_TOOL_NAME, t[0])
                put(COL_TOOL_BRAND, t[1])
                put(COL_TOOL_CATEGORY, t[2])
                put(COL_TOOL_CONDITION, t[3])
                put(COL_TOOL_STATUS, t[4])
                put(COL_TOOL_OWNER, "test")
            }
            db.insert(TABLE_TOOLS, null, cv)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BORROWS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TOOLS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // ─────────────────────────────────────────
    // USER OPERATIONS
    // ─────────────────────────────────────────

    /** Returns true if the username+password pair exists. */
    fun checkLogin(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_USER_ID),
            "$COL_USERNAME = ? AND $COL_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )
        val found = cursor.count > 0
        cursor.close()
        return found
    }

    /** Returns null on success, or an error message string. */
    fun registerUser(username: String, password: String): String? {
        if (username.isBlank() || password.isBlank()) return "Fields cannot be empty"
        val db = writableDatabase
        return try {
            val cv = ContentValues().apply {
                put(COL_USERNAME, username)
                put(COL_PASSWORD, password)
            }
            val result = db.insertOrThrow(TABLE_USERS, null, cv)
            if (result == -1L) "Registration failed" else null
        } catch (e: Exception) {
            "Username already taken"
        }
    }

    // ─────────────────────────────────────────
    // TOOL OPERATIONS
    // ─────────────────────────────────────────

    /** Fetch all tools owned by a username as an ArrayList. */
    fun getToolsByOwner(username: String): ArrayList<Tool> {
        val list = ArrayList<Tool>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TOOLS, null,
            "$COL_TOOL_OWNER = ?", arrayOf(username),
            null, null, null
        )
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Tool(
                        id       = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOOL_ID)),
                        name     = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_NAME)),
                        brand    = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_BRAND)),
                        category = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_CATEGORY)),
                        condition= cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_CONDITION)),
                        status   = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_STATUS)),
                        ownerUsername = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_OWNER))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    /** Fetch all tools NOT owned by username (for nearby/browse). */
    fun getAvailableTools(excludeUsername: String): ArrayList<Tool> {
        val list = ArrayList<Tool>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TOOLS, null,
            "$COL_TOOL_OWNER != ? AND $COL_TOOL_STATUS = 'Available'",
            arrayOf(excludeUsername),
            null, null, null
        )
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Tool(
                        id       = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOOL_ID)),
                        name     = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_NAME)),
                        brand    = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_BRAND)),
                        category = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_CATEGORY)),
                        condition= cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_CONDITION)),
                        status   = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_STATUS)),
                        ownerUsername = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_OWNER))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    /** Insert a new tool. Returns the new row id, or -1 on failure. */
    fun addTool(tool: Tool): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_TOOL_NAME, tool.name)
            put(COL_TOOL_BRAND, tool.brand)
            put(COL_TOOL_CATEGORY, tool.category)
            put(COL_TOOL_CONDITION, tool.condition)
            put(COL_TOOL_STATUS, tool.status)
            put(COL_TOOL_OWNER, tool.ownerUsername)
        }
        return db.insert(TABLE_TOOLS, null, cv)
    }

    /** Delete a tool by its id. Returns number of rows deleted. */
    fun deleteTool(toolId: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_TOOLS, "$COL_TOOL_ID = ?", arrayOf(toolId.toString()))
    }

    // ─────────────────────────────────────────
    // BORROW OPERATIONS
    // ─────────────────────────────────────────

    /** Get active borrows for a borrower username. */
    fun getActiveBorrows(borrowerUsername: String): ArrayList<Tool> {
        val list = ArrayList<Tool>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT t.* FROM $TABLE_TOOLS t
            INNER JOIN $TABLE_BORROWS b ON t.$COL_TOOL_ID = b.$COL_BORROW_TOOL_ID
            WHERE b.$COL_BORROW_BORROWER = ? AND b.$COL_BORROW_STATUS = 'Active'
        """.trimIndent(), arrayOf(borrowerUsername))
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Tool(
                        id       = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOOL_ID)),
                        name     = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_NAME)),
                        brand    = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_BRAND)),
                        category = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_CATEGORY)),
                        condition= cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_CONDITION)),
                        status   = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_STATUS)),
                        ownerUsername = cursor.getString(cursor.getColumnIndexOrThrow(COL_TOOL_OWNER))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
