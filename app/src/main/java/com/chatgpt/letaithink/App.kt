package com.chatgpt.letaithink

import android.app.Application
import com.chatgpt.letaithink.data.AppDatabase
import com.chatgpt.letaithink.data.ResourcesRepository
import com.openai.api.utils.NetworkUtils
import com.chatgpt.letaithink.utils.NotificationUtils
import com.chatgpt.letaithink.manager.PaymentManager
import com.chatgpt.letaithink.utils.ShortCutUtils
import com.google.firebase.FirebaseApp

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        AppInstallValidator.validateAppInstallation(this)

        FirebaseApp.initializeApp(this)
        AppDatabase.initDatabase(this)
        NetworkUtils.init(this)
        NotificationUtils.init(this)
        ShortCutUtils.init(this)
        ResourcesRepository.init(this)
        PaymentManager.init(this)
    }
}