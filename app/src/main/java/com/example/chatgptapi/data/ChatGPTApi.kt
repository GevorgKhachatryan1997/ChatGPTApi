package com.example.chatgptapi.data

import com.example.chatgptapi.model.*
import com.example.chatgptapi.model.remoteModelts.CompletionRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// TODO Set authorization header in one place
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