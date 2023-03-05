package com.example.chatgptapi.data

import androidx.room.*
import com.example.chatgptapi.model.databaseModels.Conversation
import com.example.chatgptapi.model.databaseModels.MessageEntity
import com.example.chatgptapi.model.databaseModels.SessionEntity
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
}