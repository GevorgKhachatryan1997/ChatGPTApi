package com.example.chatgptapi.data

import com.example.chatgptapi.model.*
import com.example.chatgptapi.model.remoteModelts.AiModels
import com.example.chatgptapi.model.remoteModelts.CompletionRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

// TODO Set authorization header in one place
// TODO Implement general exception handler
interface ChatGPTApi {

    companion object{
        const val BASE_URL = "https://api.openai.com/v1/"
        private const val TOKEN_KAY = "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"
    }

    @Headers("Authorization: Bearer $TOKEN_KAY")
    @GET("models")
    fun getModels(): Call<AiModels> // TODO replace Call with Result

    @Headers("Authorization: Bearer $TOKEN_KAY")
    @POST("completions")
    fun requestCompletion(@Body competition: CompletionRequest): Call<TextCompletion>

    @Headers("Authorization: Bearer $TOKEN_KAY")
    @POST("images/generations")
    fun requestImageGeneration(@Body body: ImageGenerationRequest): Call<ImageModel>
}