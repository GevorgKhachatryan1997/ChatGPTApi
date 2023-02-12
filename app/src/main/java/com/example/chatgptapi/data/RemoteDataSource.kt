package com.example.chatgptapi.data

import com.example.chatgptapi.data.ChatGPTApi.Companion.BASE_URL
import com.example.chatgptapi.model.AiModels
import com.example.chatgptapi.model.CompletionRequest
import com.example.chatgptapi.model.TextCompletion
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {

    companion object {
        const val DAVINSI_ID = "text-davinci-003"
        const val CURIE_ID = "text-curie-001"
        const val BABBAGE_ID = "text-babbage-001"
        const val ADA_ID = "text-ada-001"
    }

    private val chatGPTService = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChatGPTApi::class.java)

    fun getModels(): AiModels? {
        val response = chatGPTService.getModels().execute()
        if (response.isSuccessful) {
            val aiModels = response.body()
            return aiModels
        }
        //TODO trow exeption for fail case
        return null
    }

    fun getCompletion(completion: CompletionRequest): TextCompletion? {
        val response = chatGPTService.requestCompletion(completion).execute()
        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }
}