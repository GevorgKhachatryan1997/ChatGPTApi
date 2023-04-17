package com.chatgpt.letaithink.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.chatgpt.letaithink.R

object EmailUtils {
    const val SUPPORT_EMAIL = "letaithink@gmail.com"

    fun composeEmail(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_EMAIL))
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, R.string.email_application_not_found, Toast.LENGTH_LONG).show()
        }
    }
}