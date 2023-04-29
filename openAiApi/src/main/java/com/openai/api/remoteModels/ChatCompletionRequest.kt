package com.openai.api.remoteModels

data class ChatCompletionRequest(
    val model: String? = null,
    val messages: List<ChatMessage>? = null,
    val temperature: Float? = null,
    val max_tokens: Int? = null,
    val user: String? = null
) {
    data class ChatMessage(
        val role: String? = null,
        val content: String? = null
    )
}