package com.chatgpt.letaithink.model.databaseModels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_key_table")
class ApiKeyEntity(
    val apiKey: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}