package com.chatgpt.letaithink.data

import androidx.room.*
import com.chatgpt.letaithink.model.databaseModels.Conversation
import com.chatgpt.letaithink.model.databaseModels.MessageEntity
import com.chatgpt.letaithink.model.databaseModels.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Insert
    suspend fun insertSession(session: SessionEntity)

    @Query("SELECT * FROM session_table WHERE sessionId = :id")
    suspend fun getSession(id: String): SessionEntity?

    @Delete
    suspend fun deleteSession(session: SessionEntity)

    @Update
    suspend fun updateSession(sessionEntity: SessionEntity)

    @Query("SELECT * FROM session_table")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM session_table WHERE sessionId = :id")
    suspend fun getConversationBySessionId(id: String): Conversation

    @Insert
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM session_table")
    suspend fun deleteAllSessions()
}