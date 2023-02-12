package com.example.chatgptapi.model

data class Choice(
    val text: String?,
    val index: Int?,
    val logprobs: Int?,
    val finish_reason: String?
)