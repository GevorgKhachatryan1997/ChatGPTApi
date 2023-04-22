package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.model.remoteModels.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAIApi {

    companion object {
        const val BASE_URL = "https://api.openai.com/v1/"
    }

    @POST("completions")
    fun requestCompletion(@Body competition: CompletionRequest): Call<TextCompletion>

    @POST("chat/completions")
    fun requestChatCompletions(@Body chatCompletion: ChatCompletionRequest): Call<ChatCompletion>

    @POST("images/generations")
    fun requestImageGeneration(@Body body: ImageGenerationRequest): Call<ImageModel>
}