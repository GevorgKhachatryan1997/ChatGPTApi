package com.chatgpt.letaithink.model.remoteModelts

data class Choice(
    val text: String?,
    val index: Int?,
    val logprobs: Int?,
    val finish_reason: String?
)