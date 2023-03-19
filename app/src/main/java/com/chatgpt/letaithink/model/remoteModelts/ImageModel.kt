package com.chatgpt.letaithink.model.remoteModelts

class ImageModel(
    val created: Long?,
    val data: List<ImageUrl>?
)

data class ImageUrl(val url: String?)