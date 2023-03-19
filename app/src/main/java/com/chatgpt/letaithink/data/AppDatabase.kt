package com.chatgpt.letaithink.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chatgpt.letaithink.model.databaseModels.ApiKeyEntity
import com.chatgpt.letaithink.model.databaseModels.MessageEntity
import com.chatgpt.letaithink.model.databaseModels.SessionEntity
import com.chatgpt.letaithink.model.databaseModels.UserEntity

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