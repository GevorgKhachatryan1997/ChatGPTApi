package com.openai.api.models

class ImageModel(
    val created: Long?,
    val data: List<ImageUrl>?
)

data class ImageUrl(val url: String?)