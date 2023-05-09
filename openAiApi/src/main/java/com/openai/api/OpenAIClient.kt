package com.openai.api

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.gson.*

class OpenAIClient(private val apiKey: String) {

    val openAIService = HttpClient(Android) {
        install(ContentNegotiation) {
            gson()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(apiKey, "")
                }
            }
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 60 * 1000L
            connectTimeoutMillis = 60 * 1000L
            requestTimeoutMillis = 60 * 1000L
        }

        defaultRequest {
            url(OpenAIManager.BASE_URL)
        }
    }
}