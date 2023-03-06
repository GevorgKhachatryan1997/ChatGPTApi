package com.example.chatgptapi.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

const val CHAT_MODE_TEXT_COMPLETION = "chat_mode_text_completion"
const val CHAT_MODE_CODE_COMPLETION = "chat_mode_code_completion"
const val CHAT_MODE_IMAGE_GENERATION = "chat_mode_image_generation"

data class ChatMode(
    val mode: String,
    @StringRes val title: Int,
    @DrawableRes val image: Int,
    val model: String,
    var selected: Boolean = false
)