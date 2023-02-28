package com.example.chatgptapi.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.chatgptapi.model.databaseModels.UserEntity

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: UserEntity)

    @Query("DELETE FROM user_table")
    fun deleteUserData()

    @Query("SELECT * FROM user_table LIMIT 1")
    fun getUser(): UserEntity?

    @Query("SELECT EXISTS(SELECT * FROM user_table)")
    fun isExists(): Boolean
}