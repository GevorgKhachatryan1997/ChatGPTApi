package com.example.chatgptapi.utils

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun View.hideKeyboard() {
    ContextCompat.getSystemService(context, InputMethodManager::class.java)?.hideSoftInputFromWindow(windowToken, 0)
}