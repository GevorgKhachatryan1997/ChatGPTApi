package com.example.chatgptapi.data

import com.example.chatgptapi.data.ChatGPTApi.Companion.BASE_URL
import com.example.chatgptapi.model.*
import com.example.chatgptapi.model.remoteModelts.AiModels
import com.example.chatgptapi.model.remoteModelts.CompletionRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// TODO add error handling for api error codes
class RemoteDataSource {

    companion object {
        private const val CONNECTION_TIMEOUT = 60L
    }

    private val defaultClient = OkHttpClient().newBuilder()
        .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .build()

    private val chatGPTService = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .client(defaultClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChatGPTApi::class.java)

    fun getModels(): AiModels? {
        val response = chatGPTService.getModels().execute()
        if (response.isSuccessful) {
            val aiModels = response.body()
            return aiModels
        }
        //TODO trow exception for fail case
        return null
    }

    fun getCompletion(completion: CompletionRequest): TextCompletion? {
        val response = chatGPTService.requestCompletion(completion).execute()
        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    fun generateImage(requestModel: ImageGenerationRequest): ImageModel? {
        val response = chatGPTService.requestImageGeneration(requestModel).execute()
        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }
}