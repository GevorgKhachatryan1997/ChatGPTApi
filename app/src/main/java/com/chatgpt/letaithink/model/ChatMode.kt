package com.chatgpt.letaithink.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

const val CHAT_MODE_TEXT_COMPLETION = "chat_mode_text_completion"
const val CHAT_MODE_CHAT_COMPLETION = "chat_mode_chat_completion"
const val CHAT_MODE_IMAGE_GENERATION = "chat_mode_image_generation"

data class ChatMode(
    val mode: String,
    @StringRes val title: Int,
    @DrawableRes val image: Int,
    val model: String,
    val temperature: Float,
    val maxTokens: Int = 0,
    var selected: Boolean = false
)