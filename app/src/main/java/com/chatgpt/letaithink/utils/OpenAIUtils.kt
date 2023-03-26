package com.chatgpt.letaithink.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object OpenAIUtils {

    private const val OPEN_AI_API_KEYS_LINK = "https://platform.openai.com/account/api-keys"

    fun navigateToGenerateApiKeyPage(context: Context) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(OPEN_AI_API_KEYS_LINK)))
    }
}