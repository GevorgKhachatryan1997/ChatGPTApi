package com.example.chatgptapi.utils

import android.content.Context
import android.os.ResultReceiver
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun View.hideKeyboard() {
    ContextCompat.getSystemService(context, InputMethodManager::class.java)?.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard(resultReceiver: ResultReceiver? = null) {
    context.getInputMethodManager()?.showSoftInput(this, 0, resultReceiver)
}

private fun Context.getInputMethodManager(): InputMethodManager? {
    return getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
}
