package com.openai.api

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
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
    }
}