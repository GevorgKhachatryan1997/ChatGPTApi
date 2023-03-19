package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.model.remoteModelts.AiModels
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface CheckApiKeyApi {

    @GET("models")
    fun checkApiKey(@Header("Authorization") authorizationHeader: String): Call<AiModels>
}