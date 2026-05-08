package com.example.toolshedd.data

data class Tool(
    val id: Int = 0,
    val name: String,
    val brand: String,
    val category: String,
    val condition: String,
    val status: String = "Available",   // "Available" | "On Loan" | "Unlisted"
    val ownerUsername: String
)
