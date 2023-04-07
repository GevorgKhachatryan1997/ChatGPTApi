package com.chatgpt.letaithink.data.dao

import androidx.room.*
import com.chatgpt.letaithink.model.databaseModels.*

@Dao
interface ApiKeyDao {
    @Insert
    suspend fun insertApiKey(apiKeyEntity: ApiKeyEntity)

    @Query("SELECT* FROM api_key_table LIMIT 1")
    suspend fun getApiKey(): ApiKeyEntity?

    @Query("DELETE FROM api_key_table")
    suspend fun deleteApiKey()
}