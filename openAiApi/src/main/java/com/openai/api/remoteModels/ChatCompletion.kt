package com.openai.api.remoteModels

import com.google.gson.annotations.SerializedName

data class ChatCompletion(
    val id: String? = null,
    @SerializedName("object")
    val data: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val usage: Usage? = null,
    val choices: List<Choice>? = null
) {
    data class Usage(
        val prompt_tokens: Int? = null,
        val completion_tokens: Int? = null,
        val total_tokens: Int? = null
    )

    data class Choice(
        val message: Message? = null,
        val finish_reason: String? = null,
        val index: Int? = null
    )

    data class Message(
        val role: String? = null,
        val content: String? = null
    )
}