package com.chatgpt.letaithink

import android.app.Application
import com.chatgpt.letaithink.data.AppDatabase
import com.chatgpt.letaithink.utils.NetworkUtils
import com.google.firebase.FirebaseApp

// TODO Change package name
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppDatabase.initDatabase(this)
        NetworkUtils.init(this)
    }
}