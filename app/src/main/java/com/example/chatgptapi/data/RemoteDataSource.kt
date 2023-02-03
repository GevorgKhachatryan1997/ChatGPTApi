package com.example.chatgptapi.data

import com.example.chatgptapi.data.ChatGPTApi.Companion.BASE_URL
import com.example.chatgptapi.model.AiModels
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {

    private val chatGPTService = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChatGPTApi::class.java)

    suspend fun getModels(): AiModels? {
        val response = chatGPTService.getModels().execute()
        if (response.isSuccessful) {
            val aiModels = response.body()
            return aiModels
        }
        //TODO trow exeption for fail case
        return null
    }
}