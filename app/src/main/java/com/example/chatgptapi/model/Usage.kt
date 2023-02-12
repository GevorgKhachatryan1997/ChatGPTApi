package com.example.chatgptapi.model

data class Usage(
    val prompt_tokens: Int?,
    val completion_tokens: Int?,
    val total_tokens: Int?
)