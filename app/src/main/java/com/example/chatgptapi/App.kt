package com.example.chatgptapi

import android.app.Application
import com.example.chatgptapi.data.AppDatabase
import com.google.firebase.FirebaseApp

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppDatabase.initDatabase(this)
    }
}