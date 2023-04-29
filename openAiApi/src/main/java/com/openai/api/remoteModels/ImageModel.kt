package com.openai.api.remoteModels

class ImageModel(
    val created: Long?,
    val data: List<ImageUrl>?
)

data class ImageUrl(val url: String?)