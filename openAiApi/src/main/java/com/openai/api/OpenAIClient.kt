package com.openai.api

import com.openai.api.interceptor.AuthorizationInterceptor
import com.openai.api.services.CheckApiKeyApi
import com.openai.api.services.OpenAIApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class OpenAIClient(private val apiKey: String) {
    companion object {
        private const val CONNECTION_TIMEOUT = 60L
    }

    val chatGPTService = createChatApi()
    val apiKeyService = checkApiKeyApi()

    private fun createChatApi(): OpenAIApi {
        return Retrofit
            .Builder()
            .baseUrl(OpenAIApi.BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIApi::class.java)
    }

    private fun checkApiKeyApi(): CheckApiKeyApi {
        return Retrofit
            .Builder()
            .baseUrl(OpenAIApi.BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CheckApiKeyApi::class.java)
    }

    private fun createHttpClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)

        builder.addInterceptor(object : AuthorizationInterceptor() {
            override fun getAPIKey(): String = apiKey
        })
        return builder.build()
    }
}