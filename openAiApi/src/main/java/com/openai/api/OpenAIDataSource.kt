package com.openai.api

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.openai.api.exception.ApiError
import com.openai.api.exception.NoConnectionException
import com.openai.api.remoteModels.*
import com.openai.api.services.OpenAIApi
import com.openai.api.utils.JsonUtil
import com.openai.api.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class OpenAIDataSource {

    @Throws(NoConnectionException::class, ApiError::class)
    fun getCompletion(completion: CompletionRequest): TextCompletion {
        NetworkUtils.ensureNetworkConnection()

        val response = OpenAIManager.openAIClient.chatGPTService.requestCompletion(completion).execute()
        if (response.isSuccessful) {
            return response.body()!!
        }

        throwAPIError(response)
    }

    @Throws(NoConnectionException::class, ApiError::class)
    fun getChatCompletion(chatCompletion: ChatCompletionRequest): ChatCompletion {
        NetworkUtils.ensureNetworkConnection()

        val response = OpenAIManager.openAIClient.chatGPTService.requestChatCompletions(chatCompletion).execute()
        if (response.isSuccessful) {
            return response.body()!!
        }

        throwAPIError(response)
    }

    @Throws(NoConnectionException::class)
    suspend fun validateApiKey(apiKey: String): Boolean = withContext(Dispatchers.IO) {
        NetworkUtils.ensureNetworkConnection()

        val response = OpenAIClient(apiKey).apiKeyService.checkApiKey().execute()
        response.code() != OpenAIApi.RESPONSE_CODE_INVALID_API_KEY
    }

    @Throws(NoConnectionException::class, ApiError::class)
    fun generateImage(requestModel: ImageGenerationRequest): ImageModel {
        NetworkUtils.ensureNetworkConnection()

        val response = OpenAIManager.openAIClient.chatGPTService.requestImageGeneration(requestModel).execute()
        if (response.isSuccessful) {
            return response.body()!!
        }

        throwAPIError(response)
    }

    @Throws(ApiError::class)
    private fun throwAPIError(response: Response<*>): Nothing {
        val errorBody = response.errorBody()?.string() ?: ""
        val message = try {
            val error = JsonUtil.fromJson(errorBody, ErrorBody::class.java)
            error.error?.message ?: run {
                Log.d(OpenAIDataSource::class.simpleName, "Error body is empty")
                ""
            }
        } catch (e: JsonSyntaxException) {
            val message = "Could not parse error body: $errorBody"
            Log.d(OpenAIDataSource::class.simpleName, message)
            message
        }

        throw ApiError(response.code(), message)
    }
}