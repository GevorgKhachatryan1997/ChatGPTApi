package com.example.chatgptapi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chatgptapi.model.databaseModels.ApiKeyEntity
import com.example.chatgptapi.model.databaseModels.MessageEntity
import com.example.chatgptapi.model.databaseModels.SessionEntity
import com.example.chatgptapi.model.databaseModels.UserEntity

@Database(entities = [SessionEntity::class, MessageEntity::class, UserEntity::class, ApiKeyEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun conversationDao(): ConversationDao
    abstract fun apiKeyDao(): ApiKeyDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun initDatabase(context: Context) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "conversation-db"
            ).build()
        }

        fun getInstance(): AppDatabase {
            return instance!!
        }
    }
}