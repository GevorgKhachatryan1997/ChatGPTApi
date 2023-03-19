package com.chatgpt.letaithink.model.databaseModels

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "message_table", foreignKeys = [ForeignKey(
        entity = SessionEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
class MessageEntity(
    val sessionId: String,
    val type: MessageType,
    val content: String,
    val time: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}