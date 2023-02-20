package com.example.chatgptapi

import android.app.Application
import com.example.chatgptapi.data.ConversationDatabase

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        ConversationDatabase.initDatabase(this)
    }
}