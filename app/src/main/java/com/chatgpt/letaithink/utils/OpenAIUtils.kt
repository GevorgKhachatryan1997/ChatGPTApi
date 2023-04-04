package com.chatgpt.letaithink.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object OpenAIUtils {

    private const val OPEN_AI_API_KEYS_LINK = "https://platform.openai.com/account/api-keys"
    private const val OPEN_AI_API_USAGE_LINK = "https://platform.openai.com/account/usage"

    fun navigateToGenerateApiKeyPage(context: Context) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(OPEN_AI_API_KEYS_LINK)))
    }

    fun navigateToUsagePage(context: Context) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(OPEN_AI_API_USAGE_LINK)))
    }
}