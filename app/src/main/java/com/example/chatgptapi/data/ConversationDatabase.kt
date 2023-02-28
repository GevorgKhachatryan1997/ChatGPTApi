package com.example.chatgptapi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chatgptapi.model.databaseModels.MessageEntity
import com.example.chatgptapi.model.databaseModels.SessionEntity

@Database(entities = [SessionEntity::class, MessageEntity::class], version = 1)
abstract class ConversationDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao

    companion object {
        private var instance: ConversationDatabase? = null

        @Synchronized
        fun initDatabase(context: Context) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                ConversationDatabase::class.java,
                "conversation-db"
            ).build()
        }

        fun getInstance(): ConversationDatabase {
            return instance!!
        }
    }
}