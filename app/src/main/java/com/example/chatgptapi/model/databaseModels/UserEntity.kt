package com.example.chatgptapi.model.databaseModels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
class UserEntity(
    val userId: String?,
    val name: String?,
    val lastname: String?,
    val email: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}