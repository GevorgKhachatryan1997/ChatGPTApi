package com.chatgpt.letaithink.data

object ApiKeyRepository {
    private val localDataSource = LocalDataSource()
    private val remoteDataSource = RemoteDataSource()

    suspend fun insertApiKey(apiKey: String) {
        localDataSource.insertApiKey(apiKey)
    }

    suspend fun deleteApiKey() {
        localDataSource.deleteApiKey()
    }

    suspend fun getApiKey(): String? = localDataSource.getApiKey()?.apiKey

    suspend fun validateApiKey(token: String): Boolean = remoteDataSource.validateApiKey(token)

    suspend fun hasApiKey(): Boolean {
        return localDataSource.getApiKey()?.apiKey != null
    }
}