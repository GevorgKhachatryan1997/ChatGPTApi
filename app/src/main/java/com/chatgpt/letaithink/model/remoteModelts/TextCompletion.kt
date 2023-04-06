package com.chatgpt.letaithink.model.remoteModelts

import com.google.gson.annotations.SerializedName

data class TextCompletion(
    val id: String? = null,
    @SerializedName("object")
    val data: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<Choice>? = null,
    val usage: Usage? = null
)