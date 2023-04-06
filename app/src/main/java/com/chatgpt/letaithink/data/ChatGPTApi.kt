package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.model.remoteModelts.TextCompletion
import com.chatgpt.letaithink.model.remoteModelts.CompletionRequest
import com.chatgpt.letaithink.model.remoteModelts.ImageGenerationRequest
import com.chatgpt.letaithink.model.remoteModelts.ImageModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// TODO Implement general exception handler
// TODO replace Call with Result
interface ChatGPTApi {

    companion object {
        const val BASE_URL = "https://api.openai.com/v1/"
    }

    @POST("completions")
    fun requestCompletion(@Body competition: CompletionRequest): Call<TextCompletion>

    @POST("images/generations")
    fun requestImageGeneration(@Body body: ImageGenerationRequest): Call<ImageModel>
}