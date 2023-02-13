package com.example.chatgptapi.model

import com.google.gson.annotations.SerializedName

class TextCompletion(
    val id: String? = null,
    @SerializedName("object")
    val data: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<Choice>? = null,
    val usage: Usage? = null
)