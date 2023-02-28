package com.example.chatgptapi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chatgptapi.model.databaseModels.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var instance: UserDatabase? = null

        @Synchronized
        fun initDatabase(context: Context) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                UserDatabase::class.java,
                "user-db"
            ).build()
        }

        fun getInstance(): UserDatabase {
            return instance!!
        }
    }
}