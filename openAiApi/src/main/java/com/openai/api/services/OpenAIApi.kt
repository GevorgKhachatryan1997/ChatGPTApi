package com.openai.api.services

import com.openai.api.remoteModels.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAIApi {

    companion object {
        const val BASE_URL = "https://api.openai.com/v1/"

        const val RESPONSE_CODE_INVALID_API_KEY = 401
        const val RESPONSE_CODE_RATE_LIMIT_REACHED = 429
        const val RESPONSE_CODE_SERVER_HAD_ERROR = 500
    }

    @POST("completions")
    fun requestCompletion(@Body competition: CompletionRequest): Call<TextCompletion>

    @POST("chat/completions")
    fun requestChatCompletions(@Body chatCompletion: ChatCompletionRequest): Call<ChatCompletion>

    @POST("images/generations")
    fun requestImageGeneration(@Body body: ImageGenerationRequest): Call<ImageModel>
}