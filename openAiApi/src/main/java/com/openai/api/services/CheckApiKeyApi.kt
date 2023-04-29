package com.openai.api.services

import com.openai.api.models.AiModels
import retrofit2.Call
import retrofit2.http.GET

interface CheckApiKeyApi {

    @GET("models")
    fun checkApiKey(): Call<AiModels>
}