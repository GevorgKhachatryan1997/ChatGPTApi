package com.example.chatgptapi.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.chatgptapi.model.databaseModels.Conversation
import com.example.chatgptapi.model.databaseModels.MessageEntity
import com.example.chatgptapi.model.databaseModels.SessionEntity

@Dao
interface ConversationDao {

    @Insert
    fun insertSession(session: SessionEntity)

    @Delete
    fun deleteSession(session: SessionEntity)

    @Query("SELECT * FROM session_table")
    fun getAllSessions(): List<SessionEntity>

    @Query("SELECT * FROM session_table WHERE sessionId = :id")
    fun getConversationBySessionId(id: String): Conversation

    @Insert
    fun insertMessage(message: MessageEntity)
}