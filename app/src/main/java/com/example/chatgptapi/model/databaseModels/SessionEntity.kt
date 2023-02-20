package com.example.chatgptapi.model.databaseModels

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "session_table", indices = [Index(value = ["sessionId"], unique = true)])
class SessionEntity(
    val sessionId: String,
    val sessionName: String,
    val userId: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}