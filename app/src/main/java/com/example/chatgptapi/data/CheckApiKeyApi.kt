package com.example.chatgptapi.data

import com.example.chatgptapi.model.remoteModelts.AiModels
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface CheckApiKeyApi {

    @GET("models")
    fun checkApiKey(@Header("Authorization") authorizationHeader: String): Call<AiModels>
}