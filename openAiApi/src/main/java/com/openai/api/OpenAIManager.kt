package com.openai.api

object OpenAIManager {

    internal lateinit var openAIClient: OpenAIClient

    fun setAPIKey(apiKey: String) {
        openAIClient = OpenAIClient(apiKey)
    }
}