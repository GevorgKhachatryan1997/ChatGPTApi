package com.chatgpt.letaithink.model.remoteModels

class CompletionRequest(
    val user: String,
    val model: String,
    val prompt: String,
    val max_tokens: Int,
    val temperature: Float,
    val top_p: Float? = null,
    val n: Int? = null,
    val stream: Boolean = false,
    val logprobs: Int? = null,
    val stop: String? = null
)