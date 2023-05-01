package com.openai.api

import android.util.Log
import com.openai.api.OpenAIManager.RESPONSE_CODE_INVALID_API_KEY
import com.openai.api.exception.ApiError
import com.openai.api.exception.NoConnectionException
import com.openai.api.models.*
import com.openai.api.utils.NetworkUtils
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OpenAIDataSource {

    @Throws(NoConnectionException::class, ApiError::class)
    suspend fun getCompletion(completion: CompletionRequest): TextCompletion {
        val response = OpenAIManager.getCompletion(completion)

        if (response.isSucceed) {
            return response.body()
        }
        throwAPIError(response)
    }

    @Throws(NoConnectionException::class, ApiError::class)
    suspend fun getChatCompletion(chatCompletion: ChatCompletionRequest): ChatCompletion {
        NetworkUtils.ensureNetworkConnection()
        val response = OpenAIManager.getChatCompletion(chatCompletion)

        if (response.isSucceed) {
            return response.body()
        }
        throwAPIError(response)
    }

    @Throws(NoConnectionException::class, ApiError::class)
    suspend fun generateImage(requestModel: ImageGenerationRequest): ImageModel {
        val response = OpenAIManager.generateImage(requestModel)

        if (response.isSucceed) {
            return response.body()
        }
        throwAPIError(response)
    }

    @Throws(NoConnectionException::class)
    suspend fun validateApiKey(apiKey: String): Boolean = withContext(Dispatchers.IO) {
        NetworkUtils.ensureNetworkConnection()

        val response = OpenAIManager.validateApiKey(apiKey)
        val status = response.status.value
        status != RESPONSE_CODE_INVALID_API_KEY
    }

    @Throws(ApiError::class)
    private suspend fun throwAPIError(response: HttpResponse): Nothing {
        val statusCode = response.status.value
        val message = try {
            val errorBody = response.body<ErrorBody>()
            errorBody.error?.message ?: run {
                Log.d(OpenAIDataSource::class.simpleName, "Error body is empty")
                ""
            }
        } catch (e: NoTransformationFoundException) {
            val message = "Could not parse error body: ${response.bodyAsText()}"
            Log.d(OpenAIDataSource::class.simpleName, message)
            message
        }
        throw ApiError(statusCode, message)
    }

    private val HttpResponse.isSucceed: Boolean
        get() = status.value in 200..299
}