package com.chatgpt.letaithink.model.remoteModelts

data class Usage(
    val prompt_tokens: Int?,
    val completion_tokens: Int?,
    val total_tokens: Int?
)