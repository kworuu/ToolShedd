package com.example.toolshedd.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "toolshedd.db"
        const val DATABASE_VERSION = 4

        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"
        const val COL_USER_BIO = "bio"
        const val COL_USER_LOCATION = "location"

        const val TABLE_TOOLS = "tools"
        const val COL_TOOL_ID = "id"
        const val COL_TOOL_NAME = "name"
        const val COL_TOOL_BRAND = "brand"
        const val COL_TOOL_CATEGORY = "category"
        const val COL_TOOL_CONDITION = "condition"
        const val COL_TOOL_STATUS = "status"
        const val COL_TOOL_OWNER = "owner_username"

        const val TABLE_BORROWS = "borrow_requests"
        const val COL_BORROW_ID = "id"
        const val COL_BORROW_TOOL_ID = "tool_id"
        const val COL_BORROW_BORROWER = "borrower_username"
        const val COL_BORROW_STATUS = "status"
        const val COL_BORROW_DATE = "date"

        const val TABLE_REVIEWS = "reviews"
        const val COL_REVIEW_ID = "id"
        const val COL_REVIEW_TARGET = "target_username"
        const val COL_REVIEW_SENDER = "sender_username"
        const val COL_REVIEW_RATING = "rating"
        const val COL_REVIEW_COMMENT = "comment"
        const val COL_TOOL_LAT = "lat"
        const val COL_TOOL_LNG = "lng"
        const val COL_TOOL_DESCRIPTION = "description"

    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT UNIQUE NOT NULL,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_USER_BIO TEXT,
                $COL_USER_LOCATION TEXT
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
                $COL_TOOL_OWNER TEXT NOT NULL,
                $COL_TOOL_LAT REAL DEFAULT 0.0,
                $COL_TOOL_LNG REAL DEFAULT 0.0,
                ${'$'}COL_TOOL_DESCRIPTION TEXT DEFAULT ''
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

        db.execSQL("""
            CREATE TABLE $TABLE_REVIEWS (
                $COL_REVIEW_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_REVIEW_TARGET TEXT NOT NULL,
                $COL_REVIEW_SENDER TEXT NOT NULL,
                $COL_REVIEW_RATING REAL NOT NULL,
                $COL_REVIEW_COMMENT TEXT
            )
        """.trimIndent())

        val user = ContentValues().apply {
            put(COL_USERNAME, "test")
            put(COL_PASSWORD, "test12345")
            put(COL_USER_BIO, "Tool enthusiast & DIY lover")
            put(COL_USER_LOCATION, "Cebu, PH")
        }
        db.insert(TABLE_USERS, null, user)

        val review = ContentValues().apply {
            put(COL_REVIEW_TARGET, "test")
            put(COL_REVIEW_SENDER, "admin")
            put(COL_REVIEW_RATING, 4.5)
            put(COL_REVIEW_COMMENT, "Great lender!")
        }
        db.insert(TABLE_REVIEWS, null, review)

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
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REVIEWS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BORROWS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TOOLS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // ─────────────────────────────────────────
    // USER OPERATIONS
    // ─────────────────────────────────────────

    fun checkLogin(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS, arrayOf(COL_USER_ID),
            "$COL_USERNAME = ? AND $COL_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )
        val found = cursor.count > 0
        cursor.close()
        return found
    }

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

    fun getUserBio(username: String): String? {
        val db = readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COL_USER_BIO), "$COL_USERNAME = ?", arrayOf(username), null, null, null)
        val bio = if (cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        return bio
    }

    fun getUserLocation(username: String): String? {
        val db = readableDatabase
        val cursor = db.query(TABLE_USERS, arrayOf(COL_USER_LOCATION), "$COL_USERNAME = ?", arrayOf(username), null, null, null)
        val location = if (cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        return location
    }

    // ─────────────────────────────────────────
    // TOOL OPERATIONS
    // ─────────────────────────────────────────

    fun getToolsByOwner(username: String): ArrayList<Tool> {
        val list = ArrayList<Tool>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TOOLS, null,
            "$COL_TOOL_OWNER = ?", arrayOf(username),
            null, null, null
        )
        if (cursor.moveToFirst()) {
            do { list.add(cursor.toTool()) } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

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
            do { list.add(cursor.toTool()) } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getToolById(toolId: Int): Tool? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TOOLS, null,
            "$COL_TOOL_ID = ?", arrayOf(toolId.toString()),
            null, null, null
        )
        val tool = if (cursor.moveToFirst()) cursor.toTool() else null
        cursor.close()
        return tool
    }

    fun addTool(tool: Tool): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_TOOL_NAME, tool.name)
            put(COL_TOOL_BRAND, tool.brand)
            put(COL_TOOL_CATEGORY, tool.category)
            put(COL_TOOL_CONDITION, tool.condition)
            put(COL_TOOL_STATUS, tool.status)
            put(COL_TOOL_OWNER, tool.ownerUsername)
            put(COL_TOOL_LAT, tool.lat)
            put(COL_TOOL_LNG, tool.lng)
            put(COL_TOOL_DESCRIPTION, tool.description)
        }
        return db.insert(TABLE_TOOLS, null, cv)
    }

    fun deleteTool(toolId: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_TOOLS, "$COL_TOOL_ID = ?", arrayOf(toolId.toString()))
    }

    fun updateToolStatus(toolId: Int, newStatus: String) {
        val db = writableDatabase
        val cv = ContentValues().apply { put(COL_TOOL_STATUS, newStatus) }
        db.update(TABLE_TOOLS, cv, "$COL_TOOL_ID = ?", arrayOf(toolId.toString()))
    }

    // ─────────────────────────────────────────
    // BORROW OPERATIONS
    // ─────────────────────────────────────────

    fun getActiveBorrows(borrowerUsername: String): ArrayList<Tool> {
        val list = ArrayList<Tool>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT t.* FROM $TABLE_TOOLS t
            INNER JOIN $TABLE_BORROWS b ON t.$COL_TOOL_ID = b.$COL_BORROW_TOOL_ID
            WHERE b.$COL_BORROW_BORROWER = ? AND b.$COL_BORROW_STATUS = 'Active'
        """.trimIndent(), arrayOf(borrowerUsername))
        if (cursor.moveToFirst()) {
            do { list.add(cursor.toTool()) } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getBorrowHistory(borrowerUsername: String): ArrayList<Tool> {
        val list = ArrayList<Tool>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT t.* FROM $TABLE_TOOLS t
            INNER JOIN $TABLE_BORROWS b ON t.$COL_TOOL_ID = b.$COL_BORROW_TOOL_ID
            WHERE b.$COL_BORROW_BORROWER = ?
            ORDER BY b.$COL_BORROW_DATE DESC
        """.trimIndent(), arrayOf(borrowerUsername))
        if (cursor.moveToFirst()) {
            do { list.add(cursor.toTool()) } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    /**
     * Returns true if [borrowerUsername] has an active borrow record for [toolId].
     * Used by ToolDetailActivity to decide whether to show "Return tool" vs "Request to borrow".
     */
    fun isActiveBorrower(toolId: Int, borrowerUsername: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_BORROWS, arrayOf(COL_BORROW_ID),
            "$COL_BORROW_TOOL_ID = ? AND $COL_BORROW_BORROWER = ? AND $COL_BORROW_STATUS = 'Active'",
            arrayOf(toolId.toString(), borrowerUsername),
            null, null, null
        )
        val found = cursor.count > 0
        cursor.close()
        return found
    }

    fun getLendsCount(username: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT COUNT(*) FROM $TABLE_BORROWS b
            INNER JOIN $TABLE_TOOLS t ON b.$COL_BORROW_TOOL_ID = t.$COL_TOOL_ID
            WHERE t.$COL_TOOL_OWNER = ?
        """.trimIndent(), arrayOf(username))
        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    fun getUserRating(username: String): Float {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_REVIEWS, arrayOf("AVG($COL_REVIEW_RATING)"),
            "$COL_REVIEW_TARGET = ?", arrayOf(username),
            null, null, null
        )
        val rating = if (cursor.moveToFirst()) cursor.getFloat(0) else 0f
        cursor.close()
        return rating
    }

    /**
     * Marks the active borrow record as "Returned" and flips the tool back to "Available".
     * Wrapped in a transaction so both updates succeed or both roll back.
     * Returns true on success.
     */
    fun returnTool(toolId: Int, borrowerUsername: String): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val borrowCv = ContentValues().apply { put(COL_BORROW_STATUS, "Returned") }
            val rows = db.update(
                TABLE_BORROWS, borrowCv,
                "$COL_BORROW_TOOL_ID = ? AND $COL_BORROW_BORROWER = ? AND $COL_BORROW_STATUS = 'Active'",
                arrayOf(toolId.toString(), borrowerUsername)
            )
            val toolCv = ContentValues().apply { put(COL_TOOL_STATUS, "Available") }
            db.update(TABLE_TOOLS, toolCv, "$COL_TOOL_ID = ?", arrayOf(toolId.toString()))
            db.setTransactionSuccessful()
            rows > 0
        } finally {
            db.endTransaction()
        }
    }

    // ─────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────

    private fun android.database.Cursor.toTool() = Tool(
        id            = getInt(getColumnIndexOrThrow(COL_TOOL_ID)),
        name          = getString(getColumnIndexOrThrow(COL_TOOL_NAME)),
        brand         = getString(getColumnIndexOrThrow(COL_TOOL_BRAND)),
        category      = getString(getColumnIndexOrThrow(COL_TOOL_CATEGORY)),
        condition     = getString(getColumnIndexOrThrow(COL_TOOL_CONDITION)),
        status        = getString(getColumnIndexOrThrow(COL_TOOL_STATUS)),
        ownerUsername = getString(getColumnIndexOrThrow(COL_TOOL_OWNER)),
        lat = getDouble(getColumnIndexOrThrow(COL_TOOL_LAT)),
        lng = getDouble(getColumnIndexOrThrow(COL_TOOL_LNG)),
        description = getString(getColumnIndexOrThrow(COL_TOOL_DESCRIPTION)),
        imageUrl    = ""  // images live in Firestore, not SQLite
    )
}