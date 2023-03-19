package com.chatgpt.letaithink.model.remoteModelts

@JvmInline
value class ImageSize(val size: String)

val IMAGE_SIZE_256 = ImageSize("256x256")
val IMAGE_SIZE_512 = ImageSize("512x512")
val IMAGE_SIZE_1024 = ImageSize("1024x1024")

class ImageGenerationRequest(
    val user: String,
    val prompt: String,
    val size: ImageSize,
    val n: Int
)