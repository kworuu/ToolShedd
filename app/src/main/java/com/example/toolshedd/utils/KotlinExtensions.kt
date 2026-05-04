package com.example.toolshedd.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.toolshedd.app.CustomApplication


fun Activity.getEditTextValue(id: Int) = findViewById<EditText>(id).text.toString()

fun Activity.app(): CustomApplication = application as CustomApplication

fun Activity.start(toClass: Class <*> ?) {
    startActivity(Intent(this, toClass))
}

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Activity.getButtonView(id: Int) = findViewById<Button>(id)

fun Activity.setTextViewText(id: Int, string: String) {
    (findViewById<TextView>(id)).setText(string)
}

fun Activity.setEditTextText(id: Int, string: String) {
    findViewById<EditText>(id).setText("$string")
}

fun Activity.enable(id: Int) {
    (findViewById<View>(id)).isEnabled = true
}

fun Activity.disable(id: Int) {
    (findViewById<View>(id)).isEnabled = false
}

