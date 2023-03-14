package com.example.chatgptapi.model

import com.example.chatgptapi.model.remoteModelts.Choice
import com.example.chatgptapi.model.remoteModelts.Usage
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