package com.openai.api

import com.openai.api.models.ChatCompletionRequest
import com.openai.api.models.CompletionRequest
import com.openai.api.models.ImageGenerationRequest
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object OpenAIManager {

    private const val BASE_URL = "https://api.openai.com/v1/"
    private const val REQUEST_MODELS_URL = "$BASE_URL/models"
    private const val REQUEST_COMPLETIONS_URL = "$BASE_URL/completions"
    private const val REQUEST_CHAT_COMPLETION_URL = "$BASE_URL/chat/completions"
    private const val REQUEST_IMAGES_GENERATION_URL = "$BASE_URL/images/generations"

    const val RESPONSE_CODE_INVALID_API_KEY = 401
    const val RESPONSE_CODE_RATE_LIMIT_REACHED = 429
    const val RESPONSE_CODE_SERVER_HAD_ERROR = 500

    private lateinit var openAIClient: OpenAIClient

    fun setAPIKey(apiKey: String) {
        openAIClient = OpenAIClient(apiKey)
    }

    suspend fun generateImage(requestModel: ImageGenerationRequest): HttpResponse {
        return openAIClient.openAIService.post(REQUEST_IMAGES_GENERATION_URL) {
            contentType(ContentType.Application.Json)
            setBody(requestModel)
        }
    }

    suspend fun getChatCompletion(chatCompletion: ChatCompletionRequest): HttpResponse {
        return openAIClient.openAIService
            .post(REQUEST_CHAT_COMPLETION_URL) {
                contentType(ContentType.Application.Json)
                setBody(chatCompletion)
            }
    }

    suspend fun validateApiKey(apiKey: String): HttpResponse {
        return HttpClient(Android).get(REQUEST_MODELS_URL) {
            header("Authorization", "Bearer $apiKey")
        }
    }

    suspend fun getCompletion(completion: CompletionRequest): HttpResponse {
        return openAIClient.openAIService.post(REQUEST_COMPLETIONS_URL) {
            contentType(ContentType.Application.Json)
            setBody(completion)
        }
    }
}