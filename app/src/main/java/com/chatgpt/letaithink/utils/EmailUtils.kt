package com.chatgpt.letaithink.utils

import android.content.Context
import android.content.Intent

object EmailUtils {
    const val SUPPORT_EMAIL = "letaithink@gmail.com"

    fun composeEmail(context: Context) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_EMAIL))
            type = "plain/text"
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}