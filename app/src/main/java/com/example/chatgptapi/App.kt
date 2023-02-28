package com.example.chatgptapi

import android.app.Application
import com.example.chatgptapi.data.ConversationDatabase
import com.example.chatgptapi.data.UserDatabase
import com.google.firebase.FirebaseApp

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        ConversationDatabase.initDatabase(this)
        UserDatabase.initDatabase(this)
    }
}