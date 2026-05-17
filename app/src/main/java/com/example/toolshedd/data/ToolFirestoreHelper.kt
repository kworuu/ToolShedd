package com.example.toolshedd.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Handles only the Firebase-specific parts of a tool:
 * image upload to Storage and metadata sync to Firestore.
 * SQLite remains the source of truth for everything else.
 */
object ToolFirestoreHelper {

    private val db      by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    /**
     * Upload image to Firebase Storage, then save tool metadata
     * (sqliteId, imageUrl, description) to Firestore.
     */
    fun uploadToolWithImage(
        sqliteToolId: Int,
        ownerUsername: String,
        description: String,
        imageUri: Uri,
        onSuccess: (imageUrl: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val ref = storage.reference
            .child("tool_images/${ownerUsername}_${sqliteToolId}_${System.currentTimeMillis()}.jpg")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    saveToFirestore(sqliteToolId, ownerUsername, description, imageUrl,
                        onSuccess = { onSuccess(imageUrl) },
                        onError   = onError
                    )
                }
            }
            .addOnFailureListener { onError(it.message ?: "Upload failed") }
    }

    /**
     * Save tool metadata without an image (description only).
     */
    fun saveToolMetadata(
        sqliteToolId: Int,
        ownerUsername: String,
        description: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        saveToFirestore(sqliteToolId, ownerUsername, description, "",
            onSuccess = { onSuccess() },
            onError   = onError
        )
    }

    private fun saveToFirestore(
        sqliteToolId: Int,
        ownerUsername: String,
        description: String,
        imageUrl: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val data = hashMapOf(
            "sqliteToolId"  to sqliteToolId,
            "ownerUsername" to ownerUsername,
            "description"   to description,
            "imageUrl"      to imageUrl
        )
        db.collection("tool_meta")
            .document(sqliteToolId.toString())
            .set(data)
            .addOnSuccessListener { onSuccess(imageUrl) }
            .addOnFailureListener { onError(it.message ?: "Firestore save failed") }
    }

    /**
     * Fetch image URL and description for a tool by its SQLite ID.
     */
    fun getToolMeta(
        sqliteToolId: Int,
        onResult: (imageUrl: String, description: String) -> Unit
    ) {
        db.collection("tool_meta")
            .document(sqliteToolId.toString())
            .get()
            .addOnSuccessListener { doc ->
                val imageUrl    = doc.getString("imageUrl")    ?: ""
                val description = doc.getString("description") ?: ""
                onResult(imageUrl, description)
            }
            .addOnFailureListener { onResult("", "") }
    }

    /**
     * Update tool status in Firestore when borrowed/returned.
     */
    fun updateStatus(sqliteToolId: Int, status: String) {
        db.collection("tool_meta")
            .document(sqliteToolId.toString())
            .update("status", status)
    }
}