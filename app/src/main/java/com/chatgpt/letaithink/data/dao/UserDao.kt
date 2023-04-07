package com.chatgpt.letaithink.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chatgpt.letaithink.model.databaseModels.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM user_table")
    suspend fun deleteUserData()

    @Query("SELECT * FROM user_table LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Query("SELECT EXISTS(SELECT * FROM user_table)")
    suspend fun isExists(): Boolean
}