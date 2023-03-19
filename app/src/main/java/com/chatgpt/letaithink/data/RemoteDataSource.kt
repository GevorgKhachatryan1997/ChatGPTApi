package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.data.ChatGPTApi.Companion.BASE_URL
import com.chatgpt.letaithink.domain.AuthorizationInterceptor
import com.chatgpt.letaithink.model.TextCompletion
import com.chatgpt.letaithink.model.remoteModelts.CompletionRequest
import com.chatgpt.letaithink.model.remoteModelts.ImageModel
import com.chatgpt.letaithink.model.remoteModelts.ImageGenerationRequest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// TODO add error handling for api error codes
class RemoteDataSource {

    companion object {
        private const val CONNECTION_TIMEOUT = 60L

        private const val RESPONSE_CODE_INVALID_API_KEY = 401
    }

    private val chatGPTService = createChatApi()
    private val apiKeyService = checkApiKeyApi()

    fun getCompletion(completion: CompletionRequest): TextCompletion? {
        val response = chatGPTService.requestCompletion(completion).execute()
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun validateApiKey(apiKey: String): Boolean = withContext(Dispatchers.IO) {
        val header = "Bearer $apiKey"
        val response = apiKeyService.checkApiKey(header).execute()

        response.code() != RESPONSE_CODE_INVALID_API_KEY
    }

    fun generateImage(requestModel: ImageGenerationRequest): ImageModel? {
        val response = chatGPTService.requestImageGeneration(requestModel).execute()
        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    private fun createChatApi(): ChatGPTApi {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(createHttpClient(AuthorizationInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatGPTApi::class.java)
    }

    private fun checkApiKeyApi(): CheckApiKeyApi {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CheckApiKeyApi::class.java)
    }

    private fun createHttpClient(interceptor: Interceptor?): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)

        if (interceptor != null) {
            builder.addInterceptor(interceptor)
        }
        return builder.build()
    }
}