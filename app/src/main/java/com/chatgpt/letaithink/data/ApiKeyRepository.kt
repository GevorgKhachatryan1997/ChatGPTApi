package com.chatgpt.letaithink.data

import com.openai.api.exception.NoConnectionException

object ApiKeyRepository {
    private val localDataSource = LocalDataSource()
    private val openAIDataSource = com.openai.api.OpenAIDataSource()

    suspend fun updateApiKey(apiKey: String) {
        with(localDataSource) {
            deleteApiKey()
            insertApiKey(apiKey)
        }
    }

    suspend fun deleteApiKey() {
        localDataSource.deleteApiKey()
    }

    suspend fun getApiKey(): String? = localDataSource.getApiKey()?.apiKey

    @Throws(NoConnectionException::class)
    suspend fun validateApiKey(token: String): Boolean = openAIDataSource.validateApiKey(token)

    suspend fun hasApiKey(): Boolean {
        return localDataSource.getApiKey()?.apiKey != null
    }
}