package com.example.chatgptapi.model.databaseModels

import androidx.room.Embedded
import androidx.room.Relation

class Conversation(
    @Embedded
    val session: SessionEntity,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "sessionId"
    )
    val messages: List<MessageEntity>
)