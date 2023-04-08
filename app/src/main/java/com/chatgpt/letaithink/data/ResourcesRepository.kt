package com.chatgpt.letaithink.data

import android.annotation.SuppressLint
import android.content.Context
import com.chatgpt.letaithink.R
import java.util.*

@SuppressLint("StaticFieldLeak")
object ResourcesRepository {

    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    fun getRandomUserWaitMessage(): String {
        return context.resources.getStringArray(R.array.user_wait_messages).random()
    }
}