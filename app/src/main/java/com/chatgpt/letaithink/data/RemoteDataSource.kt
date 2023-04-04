package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.data.ChatGPTApi.Companion.BASE_URL
import com.chatgpt.letaithink.domain.AuthorizationInterceptor
import com.chatgpt.letaithink.exception.ApiError
import com.chatgpt.letaithink.exception.NoConnectionException
import com.chatgpt.letaithink.model.TextCompletion
import com.chatgpt.letaithink.model.remoteModelts.CompletionRequest
import com.chatgpt.letaithink.model.remoteModelts.ErrorBody
import com.chatgpt.letaithink.model.remoteModelts.ImageGenerationRequest
import com.chatgpt.letaithink.model.remoteModelts.ImageModel
import com.chatgpt.letaithink.utils.JsonUtil
import com.chatgpt.letaithink.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// TODO add error handling for api error codes
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

    @Throws(ApiError::class)
    private fun throwAPIError(response: Response<*>): Nothing {
        val errorBody = response.errorBody()?.string() ?: ""
        // TODO handle json parse exception
        val error = JsonUtil.fromJson(errorBody, ErrorBody::class.java)
        throw ApiError(response.code(), error.error?.message ?: "")
    }
}