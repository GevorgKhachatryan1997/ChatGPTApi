package com.example.chatgptapi.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ApiKeyRepository {
    private val localDataSource = LocalDataSource()
    private val remoteDataSource = RemoteDataSource()

    suspend fun insertApiKey(apiKey: String) = withContext(Dispatchers.IO) {
        localDataSource.insertApiKey(apiKey)
    }

    suspend fun deleteApiKey() = withContext(Dispatchers.IO) {
        localDataSource.deleteApiKey()
    }

    suspend fun getApiKey(): String? = withContext(Dispatchers.IO) {
        localDataSource.getApiKey()?.apiKey
    }

    suspend fun validateApiKey(token: String): Boolean = withContext(Dispatchers.IO) {
        remoteDataSource.validateApiKey(token)
    }

    suspend fun hasApiKey(): Boolean {
        return localDataSource.getApiKey()?.apiKey != null
    }
}