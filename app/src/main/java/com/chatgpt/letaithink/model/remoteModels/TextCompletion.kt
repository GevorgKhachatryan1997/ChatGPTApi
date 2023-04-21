package com.chatgpt.letaithink.model.remoteModels

import com.google.gson.annotations.SerializedName

data class TextCompletion(
    val id: String? = null,
    @SerializedName("object")
    val data: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<Choice>? = null,
    val usage: Usage? = null
) {
    data class Choice(
        val text: String? = null,
        val index: Int? = null,
        val logprobs: Int? = null,
        val finish_reason: String? = null
    )

    data class Usage(
        val prompt_tokens: Int?,
        val completion_tokens: Int?,
        val total_tokens: Int?
    )
}