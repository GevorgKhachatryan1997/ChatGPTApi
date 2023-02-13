package com.example.chatgptapi.model

class ImageModel(
    val created: Long?,
    val data: List<ImageUrl>?
)

data class ImageUrl(val url: String?)