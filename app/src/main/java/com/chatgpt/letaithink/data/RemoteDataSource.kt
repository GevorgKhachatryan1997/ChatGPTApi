package com.chatgpt.letaithink.data

import android.util.Log
import com.chatgpt.letaithink.data.ChatGPTApi.Companion.BASE_URL
import com.chatgpt.letaithink.data.OpenAIApi.Companion.BASE_URL
import com.chatgpt.letaithink.domain.AuthorizationInterceptor
import com.chatgpt.letaithink.exception.ApiError
import com.chatgpt.letaithink.exception.NoConnectionException
import com.chatgpt.letaithink.model.remoteModels.*
import com.chatgpt.letaithink.utils.JsonUtil
import com.chatgpt.letaithink.utils.NetworkUtils
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RemoteDataSource {

    companion object {
        const val MAX_TOKEN_COUNT = 1000
        private const val CONNECTION_TIMEOUT = 60L
        const val RESPONSE_CODE_INVALID_API_KEY = 401
        const val RESPONSE_CODE_RATE_LIMIT_REACHED = 429
        const val RESPONSE_CODE_SERVER_HAD_ERROR = 500
    }

    private val chatGPTService = createChatApi()
    private val apiKeyService = checkApiKeyApi()

    @Throws(NoConnectionException::class, ApiError::class)
    fun getCompletion(completion: CompletionRequest): TextCompletion {
        NetworkUtils.ensureNetworkConnection()

        val response = chatGPTService.requestCompletion(completion).execute()
        if (response.isSuccessful) {
            return response.body()!!
        }

        throwAPIError(response)
    }

    @Throws(NoConnectionException::class, ApiError::class)
    fun getChatCompletion(chatCompletion: ChatCompletionRequest): ChatCompletion {
        NetworkUtils.ensureNetworkConnection()

        val response = chatGPTService.requestChatCompletions(chatCompletion).execute()
        if (response.isSuccessful) {
            return response.body()!!
        }

        throwAPIError(response)
    }

    @Throws(NoConnectionException::class)
    suspend fun validateApiKey(apiKey: String): Boolean = withContext(Dispatchers.IO) {
        NetworkUtils.ensureNetworkConnection()

        val header = "Bearer $apiKey"
        val response = apiKeyService.checkApiKey(header).execute()

        response.code() != RESPONSE_CODE_INVALID_API_KEY
    }

    @Throws(NoConnectionException::class, ApiError::class)
    fun generateImage(requestModel: ImageGenerationRequest): ImageModel {
        NetworkUtils.ensureNetworkConnection()

        val response = chatGPTService.requestImageGeneration(requestModel).execute()
        if (response.isSuccessful) {
            return response.body()!!
        }

        throwAPIError(response)
    }

    private fun createChatApi(): OpenAIApi {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(createHttpClient(AuthorizationInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIApi::class.java)
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

    @Throws(ApiError::class)
    private fun throwAPIError(response: Response<*>): Nothing {
        val errorBody = response.errorBody()?.string() ?: ""
        val message = try {
            val error = JsonUtil.fromJson(errorBody, ErrorBody::class.java)
            error.error?.message ?: run {
                Log.d(RemoteDataSource::class.simpleName, "Error body is empty")
                ""
            }
        } catch (e: JsonSyntaxException) {
            val message = "Could not parse error body: $errorBody"
            Log.d(RemoteDataSource::class.simpleName, message)
            message
        }

        throw ApiError(response.code(), message)
    }
}